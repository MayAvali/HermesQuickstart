package org.firstinspires.ftc.teamcode.HermesFTC.examples;

import com.acmerobotics.roadrunner.ftc.DisplacementFollower;
import com.acmerobotics.roadrunner.ftc.Follower;
import com.acmerobotics.roadrunner.geometry.Arclength;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectories.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.HermesFTC.MecanumDrive;

@Autonomous
@Disabled
public class FollowerExample extends OpMode {
    private MecanumDrive drive;
    private Follower follower;

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, new Pose2d(0.0, 0.0, 0.0));
        Trajectory<Arclength> traj = drive.trajectoryBuilder()
                .forward(10.0)
                .splineTo(new Vector2d(10.0, 10.0), Math.toRadians(90.0))
                .buildToComposite();

        follower = new DisplacementFollower(traj, drive);
    }

    @Override
    public void loop() {
        if (!follower.isDone()) {
            follower.follow();
        }
    }
}
