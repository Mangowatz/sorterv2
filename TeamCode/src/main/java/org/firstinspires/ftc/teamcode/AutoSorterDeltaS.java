package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by Zack on 4/29/19.
 * Adapted from Aryeh's 2017-2018 FTC
 */
@Autonomous(name = "DeltaS", group = "Autonomous")

public class AutoSorterDeltaS extends LinearOpMode {

    static final int block1 = 15;//4x2
    static final int block2 = 26;//tape
    static final int block3 = 5;//3x2
    private static final double COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    private static final double DRIVE_GEAR_REDUCTION    = 2.0 ;     // This is < 1.0 if geared UP
    private static final double WHEEL_DIAMETER_INCHES   = 1.375 ;     // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    private ColorSensor jimmyTheSensor;
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor conveyorBelt;
    private Servo separator;

    @Override
    public void runOpMode(){


        //Setup Hardware
        hardwareSetup();
        waitForStart();
        while(opModeIsActive()) {
            colorFeedback();
            conveyorBelt.setPower(1);
            telemetry.update();

            while ((jimmyTheSensor.red() >= 3) && (jimmyTheSensor.green() >= 3) && (jimmyTheSensor.blue() >= 3)) {//there is a block
                getLegnth();
            }
            deposit();
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
    int distanceCount;

    public void colorFeedback(){

        telemetry.addData("red: ", jimmyTheSensor.red());
        telemetry.addData("green: ", jimmyTheSensor.green());
        telemetry.addData("blue: ", jimmyTheSensor.blue());
        if((jimmyTheSensor.red() >= 4) && (jimmyTheSensor.green() >= 4) && (jimmyTheSensor.blue() >= 4)){
            telemetry.addData("No object",0);
        }
        telemetry.update();
    }

    protected void getLegnth(){ //this code continually runs when there is a block. distanceCount represents object distance in in. in theory,  at least

        encoderDrive(.5,.1,2);
        distanceCount++;
        telemetry.addData("Measuring Distance: ",distanceCount);
        telemetry.update();

    }
    private void deposit() { //thank you for distance; now it will go into box
        if ((distanceCount <= block1 + 5) && (distanceCount >= block1 - 5)) {//block 1
            telemetry.addData("Block chosen: ",1);
            separator.setPosition(.33);
            encoderDrive(1,18,5);

        } else if ((distanceCount <= block2 + 5) && (distanceCount >= block2 - 5)) {//block 2
            telemetry.addData("Block chosen: ",2);
            separator.setPosition(.5);
            encoderDrive(1,18,5);

        } else if ((distanceCount <= block3 + 5) && (distanceCount >= block3 - 5)) {//block 3
            telemetry.addData("Block chosen: ",3);
            separator.setPosition(.7);
            encoderDrive(1,18,5);
        } else{
            telemetry.addData("Unknown Object",0);
        }
        distanceCount = 0;
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

            while (opModeIsActive() && (runtime.seconds() < timeoutS) && (conveyorBelt.isBusy()));

            // Stop all motion;
            conveyorBelt.setPower(0);

            // Turn off RUN_TO_POSITION
            conveyorBelt.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}


