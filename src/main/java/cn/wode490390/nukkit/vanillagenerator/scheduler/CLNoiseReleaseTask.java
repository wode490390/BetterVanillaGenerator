package cn.wode490390.nukkit.vanillagenerator.scheduler;

import cn.nukkit.scheduler.AsyncTask;
import com.jogamp.opencl.CLBuffer;

import java.nio.FloatBuffer;

public class CLNoiseReleaseTask extends AsyncTask {

    private CLBuffer<FloatBuffer> noise;

    public CLNoiseReleaseTask(CLBuffer<FloatBuffer> noise) {
        this.noise = noise;
    }

    @Override
    public void onRun() {
        this.noise.release();
    }
}
