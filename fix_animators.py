import os
import glob
import re

camera_state_content = """package com.alrex.parcool.client.animation;

public class CameraState {
    private float pitch;
    private float yaw;
    private float roll;
    private final float partialTick;

    public CameraState(float pitch, float yaw, float roll, float partialTick) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.partialTick = partialTick;
    }

    public float getPitch() { return pitch; }
    public void setPitch(float pitch) { this.pitch = pitch; }
    public float getYaw() { return yaw; }
    public void setYaw(float yaw) { this.yaw = yaw; }
    public float getRoll() { return roll; }
    public void setRoll(float roll) { this.roll = roll; }
    public float getPartialTick() { return partialTick; }
}
"""

with open("src/main/java/com/alrex/parcool/client/animation/CameraState.java", "w") as f:
    f.write(camera_state_content)

files = glob.glob("src/main/java/com/alrex/parcool/client/animation/**/*.java", recursive=True)
files.append("src/main/java/com/alrex/parcool/common/attachment/client/Animation.java")
files.append("src/main/java/com/alrex/parcool/common/action/ActionProcessor.java")

for file in files:
    with open(file, "r") as f:
        content = f.read()
    
    content = re.sub(r'import net\.neoforged\..+;\n', '', content)
    content = content.replace('ViewportEvent.ComputeCameraAngles', 'CameraState')
    content = content.replace('RenderFrameEvent.Pre', 'Float')
    content = content.replace('event.getPartialTick()', 'event.getPartialTick()')
    content = content.replace('Float event', 'Float tickDelta')
    
    with open(file, "w") as f:
        f.write(content)

