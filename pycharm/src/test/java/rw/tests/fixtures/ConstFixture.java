package rw.tests.fixtures;

import rw.consts.Const;
import rw.consts.Stage;

import static org.mockito.Mockito.spy;


public class ConstFixture {
    Const device;

    boolean makeProd;

    public ConstFixture(boolean makeProd) {
        this.makeProd = makeProd;
    }

    public void start() throws Exception {
        this.device = spy(new Const());
        Const.singleton = this.device;

        if (this.makeProd) {
            this.device.stage = Stage.PROD;
        }
    }

    public void tearDown() throws Exception {
    }
}

