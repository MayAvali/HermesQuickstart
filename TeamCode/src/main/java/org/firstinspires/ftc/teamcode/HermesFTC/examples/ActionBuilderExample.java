package org.firstinspires.ftc.teamcode.HermesFTC.examples;

import com.acmerobotics.roadrunner.actions.Action;
import com.acmerobotics.roadrunner.actions.ActionRunner;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HermesFTC.MecanumDrive;

@Autonomous
@Disabled
public class ActionBuilderExample extends LinearOpMode {
    MecanumDrive drive;
    Action action;

    @Override
    public void runOpMode() throws InterruptedException {
        drive = new MecanumDrive(hardwareMap, new Pose2d(0.0, 0.0, 0.0));
        action = drive.actionBuilder()
                .forward(10.0)
                .splineTo(new Vector2d(10.0, 10.0), Math.toRadians(90.0))
                .build();

        waitForStart();

        ActionRunner.runBlocking(action);
    }
}
