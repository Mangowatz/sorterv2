package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Zack on 4/29/19.
 * Adapted from Aryeh's 2017-2018 FTC code
 */
@Autonomous(name = "rgb", group = "Autonomous")
public class AutoSorterRGB extends LinearOpMode {

    static final int block1R = 50;
    static final int block1G = 50;
    static final int block1B = 50;

    static final int block2R = 1;
    static final int block2G = 1;
    static final int block2B = 1;

    static final int block3R = 1;
    static final int block3G = 1;
    static final int block3B = 1;


    private static final double COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    private static final double DRIVE_GEAR_REDUCTION    = 2.0 ;     // This is < 1.0 if geared UP
    private static final double WHEEL_DIAMETER_INCHES   = 1.375 ;     // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    private ColorSensor jimmyTheSensor;
    private DcMotor conveyorBelt;
    private Servo separator;
    private ElapsedTime runtime = new ElapsedTime();


    @Override
    public void runOpMode() {

        //Setup Hardware
        hardwareSetup();
        waitForStart();
        while (opModeIsActive()) {
            colorFeedback();
            conveyorBelt.setPower(1);
            while (!(jimmyTheSensor.red() >= 20) && !(jimmyTheSensor.green() >= 20) && !(jimmyTheSensor.blue() >= 20)) {//there is a block
                conveyorBelt.setPower(0);
                deposit();
                telemetry.update();
            }
        }
    }

    private void hardwareSetup(){
        jimmyTheSensor = hardwareMap.get(ColorSensor.class, "Jimmy");
        conveyorBelt = hardwareMap.get(DcMotor.class, "Conveyor Belt");
        conveyorBelt.setDirection(DcMotor.Direction.FORWARD);
        conveyorBelt.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        separator = hardwareMap.get(Servo.class, "Separator");
        telemetry.addData("Status: ", "Initialized");
        telemetry.update();
    }

    public void colorFeedback(){

        telemetry.addData("red: ", jimmyTheSensor.red());
        telemetry.addData("green: ", jimmyTheSensor.green());
        telemetry.addData("blue: ", jimmyTheSensor.blue());
        if((jimmyTheSensor.red() >= 20) && (jimmyTheSensor.green() >= 20) && (jimmyTheSensor.blue() >= 20)){
            telemetry.addData("No object",0);//hi
        }
        telemetry.update();
    }
    private void deposit(){
        if(/*color matches up with brick1*/
                        (jimmyTheSensor.red() >= block1R +10)&&(jimmyTheSensor.red() <= block1R-10)
                        &&(jimmyTheSensor.green() >= block1G +10)&&(jimmyTheSensor.green() <= block1G-10)
                        &&(jimmyTheSensor.blue() >= block1B +10)&&(jimmyTheSensor.blue() <= block1B-10))
            {
                telemetry.addData("Block Chosen: ",1);
                encoderDrive(1,18,5);
                separator.setPosition(.33);
            }

        else if(/*color matches up with brick2*/
                        (jimmyTheSensor.red() >= block2R +10)&&(jimmyTheSensor.red() <= block2R-10)
                        &&(jimmyTheSensor.green() >= block2G +10)&&(jimmyTheSensor.green() <= block2G-10)
                        &&(jimmyTheSensor.blue() >= block2B +10)&&(jimmyTheSensor.blue() <= block2B-10))
            {
                telemetry.addData("Block Chosen: ",2);
                encoderDrive(1,18,5);
                separator.setPosition(.5);
            }

        else if(/*color matches up with brick3*/
                        (jimmyTheSensor.red() >= block3R +10)&&(jimmyTheSensor.red() <= block3R-10)
                        &&(jimmyTheSensor.green() >= block3G +10)&&(jimmyTheSensor.green() <= block3G-10)
                        &&(jimmyTheSensor.blue() >= block3B +10)&&(jimmyTheSensor.blue() <= block3B-10))
            {
                telemetry.addData("Block Chosen: ",3);
                encoderDrive(1,18,5);
                separator.setPosition(.7);
            }

        else{
            telemetry.addData("ERROR: ",1);
            telemetry.addData("UNKNOWN OBJECT",0);
        }

    }
    public void encoderDrive(double speed,
                             double leftInches,
                             double timeoutS) {
        int newLeftTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = conveyorBelt.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);


            conveyorBelt.setTargetPosition(newLeftTarget);

            // Turn On RUN_TO_POSITION
            conveyorBelt.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            conveyorBelt.setPower(Math.abs(speed));


            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (conveyorBelt.isBusy())) {

            }

            // Stop all motion;
            conveyorBelt.setPower(0);


            // Turn off RUN_TO_POSITION
            conveyorBelt.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        }
    }
}