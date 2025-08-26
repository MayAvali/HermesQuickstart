package org.firstinspires.ftc.teamcode.HermesFTC.tuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.reflection.ReflectionConfig;
import com.acmerobotics.roadrunner.control.MotorFeedforward;
import com.acmerobotics.roadrunner.ftc.*;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.hardware.*;
import com.acmerobotics.roadrunner.tuning.*;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.teamcode.HermesFTC.MecanumDrive;
import org.firstinspires.ftc.teamcode.HermesFTC.TankDrive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TuningOpModes {
    // TODO: change this to TankDrive.class if you're using tank
    public static final Class<?> DRIVE_CLASS = MecanumDrive.class;

    public static final String GROUP = "quickstart";
    public static final boolean DISABLED = false;

    private TuningOpModes() {}

    private static OpModeMeta metaForClass(Class<? extends OpMode> cls) {
        return new OpModeMeta.Builder()
                .setName(cls.getSimpleName())
                .setGroup(GROUP)
                .setFlavor(OpModeMeta.Flavor.TELEOP)
                .build();
    }

    @OpModeRegistrar
    public static void register(OpModeManager manager) {
        if (DISABLED) return;

        DriveViewFactory dvf;
        if (DRIVE_CLASS.equals(MecanumDrive.class)) {
            dvf = hardwareMap -> {
                MecanumDrive md = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
                LazyImu lazyImu = md.lazyImu;

                List<EncoderGroup> encoderGroups = new ArrayList<>();
                List<EncoderRef> leftEncs = new ArrayList<>(), rightEncs = new ArrayList<>();
                List<EncoderRef> parEncs = new ArrayList<>(), perpEncs = new ArrayList<>();
                if (md.localizer instanceof MecanumDriveLocalizer) {
                    MecanumDriveLocalizer dl = (MecanumDriveLocalizer) md.localizer;
                    encoderGroups.add(new LynxQuadratureEncoderGroup(
                            hardwareMap.getAll(LynxModule.class),
                            Arrays.asList(dl.getLeftFront(), dl.getLeftBack(), dl.getRightFront(), dl.getRightBack())
                    ));
                    leftEncs.add(new EncoderRef(0, 0));
                    leftEncs.add(new EncoderRef(0, 1));
                    rightEncs.add(new EncoderRef(0, 2));
                    rightEncs.add(new EncoderRef(0, 3));
                } else if (md.localizer instanceof ThreeDeadWheelLocalizer) {
                    ThreeDeadWheelLocalizer dl = (ThreeDeadWheelLocalizer) md.localizer;
                    encoderGroups.add(new LynxQuadratureEncoderGroup(
                            hardwareMap.getAll(LynxModule.class),
                            Arrays.asList(dl.getPar0(), dl.getPar1(), dl.getPerp())
                    ));
                    parEncs.add(new EncoderRef(0, 0));
                    parEncs.add(new EncoderRef(0, 1));
                    perpEncs.add(new EncoderRef(0, 2));
                } else if (md.localizer instanceof TwoDeadWheelLocalizer) {
                    TwoDeadWheelLocalizer dl = (TwoDeadWheelLocalizer) md.localizer;
                    encoderGroups.add(new LynxQuadratureEncoderGroup(
                            hardwareMap.getAll(LynxModule.class),
                            Arrays.asList(dl.getPar(), dl.getPerp())
                    ));
                    parEncs.add(new EncoderRef(0, 0));
                    perpEncs.add(new EncoderRef(0, 1));
                } else if (md.localizer instanceof OTOSLocalizer) {
                    OTOSLocalizer ol = (OTOSLocalizer) md.localizer;
                    encoderGroups.add(new OTOSEncoderGroup(ol.getOtos()));
                    parEncs.add(new EncoderRef(0, 0));
                    perpEncs.add(new EncoderRef(0, 1));
                    lazyImu = new OTOSIMU(ol.getOtos());
                }  else if (md.localizer instanceof PinpointLocalizer) {
                    PinpointLocalizer pl = (PinpointLocalizer) md.localizer;
                    encoderGroups.add(new PinpointEncoderGroup(pl));
                    parEncs.add(new EncoderRef(0, 0));
                    perpEncs.add(new EncoderRef(0, 1));
                    lazyImu = new PinpointIMU(pl);
                } else {
                    throw new RuntimeException("unknown localizer: " + md.localizer.getClass().getName());
                }

                return new DriveView(
                    DriveType.MECANUM,
                        MecanumDrive.PARAMS.inPerTick,
                        MecanumDrive.PARAMS.maxWheelVel,
                        MecanumDrive.PARAMS.minProfileAccel,
                        MecanumDrive.PARAMS.maxProfileAccel,
                        encoderGroups,
                        Arrays.asList(
                                md.leftFront,
                                md.leftBack
                        ),
                        Arrays.asList(
                                md.rightFront,
                                md.rightBack
                        ),
                        leftEncs,
                        rightEncs,
                        parEncs,
                        perpEncs,
                        lazyImu,
                        md.voltageSensor,
                        () -> new MotorFeedforward(MecanumDrive.PARAMS.kS,
                                MecanumDrive.PARAMS.kV / MecanumDrive.PARAMS.inPerTick,
                                MecanumDrive.PARAMS.kA / MecanumDrive.PARAMS.inPerTick),
                        0
                );
            };
        } else if (DRIVE_CLASS.equals(TankDrive.class)) {
            dvf = hardwareMap -> {
                TankDrive td = new TankDrive(hardwareMap, new Pose2d(0, 0, 0));
                LazyImu lazyImu = td.lazyImu;

                List<EncoderGroup> encoderGroups = new ArrayList<>();
                List<EncoderRef> leftEncs = new ArrayList<>(), rightEncs = new ArrayList<>();
                List<EncoderRef> parEncs = new ArrayList<>(), perpEncs = new ArrayList<>();
                if (td.localizer instanceof TankLocalizer) {
                    TankLocalizer dl = (TankLocalizer) td.localizer;
                    List<Encoder> allEncoders = new ArrayList<>();
                    allEncoders.addAll(dl.getLeftEncoders());
                    allEncoders.addAll(dl.getRightEncoders());
                    encoderGroups.add(new LynxQuadratureEncoderGroup(
                            hardwareMap.getAll(LynxModule.class),
                            allEncoders
                    ));
                    for (int i = 0; i < dl.getLeftEncoders().size(); i++) {
                        leftEncs.add(new EncoderRef(0, i));
                    }
                    for (int i = 0; i < dl.getRightEncoders().size(); i++) {
                        rightEncs.add(new EncoderRef(0, dl.getLeftEncoders().size() + i));
                    }
                } else if (td.localizer instanceof ThreeDeadWheelLocalizer) {
                    ThreeDeadWheelLocalizer dl = (ThreeDeadWheelLocalizer) td.localizer;
                    encoderGroups.add(new LynxQuadratureEncoderGroup(
                            hardwareMap.getAll(LynxModule.class),
                            Arrays.asList(dl.getPar0(), dl.getPar1(), dl.getPerp())
                    ));
                    parEncs.add(new EncoderRef(0, 0));
                    parEncs.add(new EncoderRef(0, 1));
                    perpEncs.add(new EncoderRef(0, 2));
                } else if (td.localizer instanceof TwoDeadWheelLocalizer) {
                    TwoDeadWheelLocalizer dl = (TwoDeadWheelLocalizer) td.localizer;
                    encoderGroups.add(new LynxQuadratureEncoderGroup(
                            hardwareMap.getAll(LynxModule.class),
                            Arrays.asList(dl.getPar(), dl.getPerp())
                    ));
                    parEncs.add(new EncoderRef(0, 0));
                    perpEncs.add(new EncoderRef(0, 1));
                }  else if (td.localizer instanceof PinpointLocalizer) {
                    PinpointLocalizer pl = (PinpointLocalizer) td.localizer;
                    encoderGroups.add(new PinpointEncoderGroup(pl));
                    parEncs.add(new EncoderRef(0, 0));
                    perpEncs.add(new EncoderRef(0, 1));
                    lazyImu = new PinpointIMU(pl);
                } else if (td.localizer instanceof OTOSLocalizer) {
                    OTOSLocalizer ol = (OTOSLocalizer) td.localizer;
                    encoderGroups.add(new OTOSEncoderGroup(ol.getOtos()));
                    parEncs.add(new EncoderRef(0, 0));
                    perpEncs.add(new EncoderRef(0, 1));
                    lazyImu = new OTOSIMU(ol.getOtos());
                } else {
                    throw new RuntimeException("unknown localizer: " + td.localizer.getClass().getName());
                }

                return new DriveView(
                        DriveType.TANK,
                        TankDrive.PARAMS.inPerTick,
                        TankDrive.PARAMS.maxWheelVel,
                        TankDrive.PARAMS.minProfileAccel,
                        TankDrive.PARAMS.maxProfileAccel,
                        encoderGroups,
                        td.leftMotors,
                        td.rightMotors,
                        leftEncs,
                        rightEncs,
                        parEncs,
                        perpEncs,
                        lazyImu,
                        td.voltageSensor,
                        () -> new MotorFeedforward(TankDrive.PARAMS.kS,
                                TankDrive.PARAMS.kV / TankDrive.PARAMS.inPerTick,
                                TankDrive.PARAMS.kA / TankDrive.PARAMS.inPerTick),
                        0
                );
            };
        } else {
            throw new RuntimeException();
        }

        manager.register(metaForClass(AngularRampLogger.class), new AngularRampLogger(dvf));
        manager.register(metaForClass(ForwardPushTest.class), new ForwardPushTest(dvf));
        manager.register(metaForClass(ForwardRampLogger.class), new ForwardRampLogger(dvf));
        manager.register(metaForClass(LateralPushTest.class), new LateralPushTest(dvf));
        manager.register(metaForClass(LateralRampLogger.class), new LateralRampLogger(dvf));
        manager.register(metaForClass(ManualFeedforwardTuner.class), new ManualFeedforwardTuner(dvf));
        manager.register(metaForClass(MecanumMotorDirectionDebugger.class), new MecanumMotorDirectionDebugger(dvf));
        manager.register(metaForClass(DeadWheelDirectionDebugger.class), new DeadWheelDirectionDebugger(dvf));

        manager.register(metaForClass(ManualFeedbackTuner.class), ManualFeedbackTuner.class);
        manager.register(metaForClass(SplineTest.class), SplineTest.class);
        manager.register(metaForClass(LocalizationTest.class), LocalizationTest.class);

        manager.register(metaForClass(OTOSAngularScalarTuner.class), new OTOSAngularScalarTuner(dvf));
        manager.register(metaForClass(OTOSLinearScalarTuner.class), new OTOSLinearScalarTuner(dvf));
        manager.register(metaForClass(OTOSHeadingOffsetTuner.class), new OTOSHeadingOffsetTuner(dvf));
        manager.register(metaForClass(OTOSPositionOffsetTuner.class), new OTOSPositionOffsetTuner(dvf));

        FtcDashboard.getInstance().withConfigRoot(configRoot -> {
            for (Class<?> c : Arrays.asList(
                    AngularRampLogger.class,
                    ForwardRampLogger.class,
                    LateralRampLogger.class,
                    ManualFeedforwardTuner.class,
                    MecanumMotorDirectionDebugger.class,
                    ManualFeedbackTuner.class
            )) {
                configRoot.putVariable(c.getSimpleName(), ReflectionConfig.createVariableFromClass(c));
            }
        });
    }
}
