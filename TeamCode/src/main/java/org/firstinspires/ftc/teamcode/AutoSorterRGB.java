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
 * For CIJE Innovation day 2019
 */

@Autonomous(name = "rgb", group = "Autonomous")
public class AutoSorterRGB extends LinearOpMode {

    static final int block1R = 8; //red block
    static final int block1G = 4;
    static final int block1B = 4;

    static final int block2R = 16;//tan
    static final int block2G = 14;
    static final int block2B = 12;

    static final int block3R = 7;//yellow
    static final int block3G = 6;
    static final int block3B = 5;


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
            telemetry.update();

            while ((jimmyTheSensor.red() >= 3) && (jimmyTheSensor.green() >= 3) && (jimmyTheSensor.blue() >= 3)) {//there is a block
                deposit();
            }
        }
    }

    private void hardwareSetup(){
        jimmyTheSensor = hardwareMap.get(ColorSensor.class, "Jimmy");
        conveyorBelt = hardwareMap.get(DcMotor.class, "Conveyor Belt");
        conveyorBelt.setDirection(DcMotor.Direction.FORWARD);
        conveyorBelt.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        separator = hardwareMap.get(Servo.class, "Separator");
        telemetry.addData("Please ensure the sensor is properly installed and there are no fingers near the machine.", "");
        telemetry.addData("Press button when your are ready", "");
        telemetry.update();
    }

    public void colorFeedback(){

        telemetry.addData("red: ", jimmyTheSensor.red());
        telemetry.addData("green: ", jimmyTheSensor.green());
        telemetry.addData("blue: ", jimmyTheSensor.blue());
        telemetry.update();
    }
    private void deposit(){
        //encoderDrive(.5,-.75,5);
        if(/*color matches up with brick1*/
                        (jimmyTheSensor.red() <= block1R +3)&&(jimmyTheSensor.red() >= block1R+3)
                        &&(jimmyTheSensor.green() <= block1G +3)&&(jimmyTheSensor.green() >= block1G+3)
                        &&(jimmyTheSensor.blue() <= block1B +3)&&(jimmyTheSensor.blue() >= block1B+3))
            {
                telemetry.addData("Block Chosen: ",1);
                telemetry.update();
                separator.setPosition(.33);
                encoderDrive(1,18,5);
            }

        else if(/*color matches up with brick2*/
                        (jimmyTheSensor.red() <= block2R +3)&&(jimmyTheSensor.red() >= block2R+3)
                        &&(jimmyTheSensor.green() <= block2G +3)&&(jimmyTheSensor.green() >= block2G+3)
                        &&(jimmyTheSensor.blue() <= block2B +3)&&(jimmyTheSensor.blue() >= block2B+3))
            {
                telemetry.addData("Block Chosen: ",2);
                telemetry.update();
                separator.setPosition(.5);
                encoderDrive(1,18,5);
            }

        else if(/*color matches up with brick3*/
                        (jimmyTheSensor.red() <= block3R +3)&&(jimmyTheSensor.red() >= block3R+3)
                        &&(jimmyTheSensor.green() <= block3G +3)&&(jimmyTheSensor.green() >= block3G+3)
                        &&(jimmyTheSensor.blue() <= block3B +3)&&(jimmyTheSensor.blue() >= block3B+3))
            {
                telemetry.addData("Block Chosen: ",3);
                telemetry.update();
                separator.setPosition(.7);
                encoderDrive(1,18,5);
            }

        else{
            telemetry.addData("UNKNOWN OBJECT",0);
            telemetry.update();
            conveyorBelt.setPower(1);
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