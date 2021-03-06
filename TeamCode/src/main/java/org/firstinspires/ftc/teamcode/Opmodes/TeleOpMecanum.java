/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.HardwareProfiles.HardwareTestPlatform;

/**
 * This OpMode uses the common Pushbot hardware class to define the devices on the robot.
 * All device access is managed through the HardwarePushbot class.
 * The code is structured as a LinearOpMode
 * <p>
 * This particular OpMode executes a POV Game style Teleop for a PushBot
 * In this mode the left stick moves the robot FWD and back, the Right stick turns left and right.
 * It raises and lowers the claw using the Gampad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 * <p>
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "COMP TeleOpRearWheelDrive OpModeMecanum", group = "COMP")

public class TeleOpMecanum extends LinearOpMode {
    /**
     * Instantiate all objects needed in this class
     */
    private final static HardwareTestPlatform robot = new HardwareTestPlatform();
    double myCurrentLauncherPosition = 0;
    int launcherEncoderStop = 0;

    @Override
    public void runOpMode() {

        begin();

        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        robot.motorShooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorShooter.setTargetPosition(0);
        robot.motorShooter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        while (robot.motorShooter.isBusy()) {
            robot.motorShooter.setPower(.4);
        }

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        telemetry.addData("TeleOpRearWheelDrive Active", "");    //
        telemetry.update();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            double r = Math.hypot((gamepad1.left_stick_x * -1), gamepad1.left_stick_y);
            double robotAngle = Math.atan2(gamepad1.left_stick_y, (gamepad1.left_stick_x * -1)) - Math.PI / 4;
            double rightX = gamepad1.right_stick_x;
            final double v1 = r * Math.cos(robotAngle) + rightX;
            final double v2 = r * Math.sin(robotAngle) - rightX;
            final double v3 = r * Math.sin(robotAngle) + rightX;
            final double v4 = r * Math.cos(robotAngle) - rightX;

            robot.motorLF.setPower(v1);
            robot.motorRF.setPower(v2);
            robot.motorLR.setPower(v3);
            robot.motorRR.setPower(v4);

            if (gamepad1.left_bumper) {
                robot.servoPusher.setPosition(1);
            }

            if (gamepad1.right_bumper) {
                robot.servoPusher.setPosition(0);
            }

            if (gamepad1.left_trigger > 0 && gamepad1.right_trigger == 0) {
                robot.motorFeeder.setPower(1);
            }

            if (gamepad1.right_trigger > 0 && gamepad1.left_trigger == 0) {
                robot.motorFeeder.setPower(-1);
            }

            if (gamepad1.right_trigger == 0 && gamepad1.left_trigger == 0) {
                robot.motorFeeder.setPower(0);
            }

            if (gamepad1.a) {
                int encoderStop = 1440;
                double myCurrentPosition;

                robot.motorShooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                robot.motorShooter.setTargetPosition(encoderStop);
                robot.motorShooter.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                while (robot.motorShooter.isBusy()) {
                    robot.motorShooter.setPower(.4);
                    myCurrentPosition = robot.motorLF.getCurrentPosition();
                    telemetry.addData("myCurrentPosition", String.valueOf(myCurrentPosition));
                    telemetry.update();
                    idle();
                }

                motorsHalt();
            }
            telemetry.addData("left_stick_x", String.valueOf(gamepad1.left_stick_x));
            telemetry.addData("left_stick_y", String.valueOf(gamepad1.left_stick_y));
            telemetry.addData("right_stick_x", String.valueOf(gamepad1.right_stick_x));
            telemetry.addData("LF", String.valueOf(v1));
            telemetry.addData("RF", String.valueOf(v2));
            telemetry.addData("LR", String.valueOf(v3));
            telemetry.addData("RR", String.valueOf(v4));
            telemetry.update();

            idle();
        }
    }

    public void motorsHalt() {
        robot.motorLF.setPower(0);
        robot.motorRF.setPower(0);
        robot.motorLR.setPower(0);
        robot.motorRR.setPower(0);
        robot.motorFeeder.setPower(0);
    }

    private void begin() {

        /**
         * Inititialize the robot's hardware.  The hardware configuration can be found in the
         * HardwareTestPlatform.java class.
         */
        robot.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }
}
