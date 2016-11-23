package kr.rvs.pscontroller.container;

import protocolsupport.api.remapper.BlockRemapperControl;

/**
 * Created by deide on 2016-11-23.
 */
public class RemapData {
    private BlockRemapperControl.MaterialAndData to;
    private BlockRemapperControl.MaterialAndData from;

    public RemapData(BlockRemapperControl.MaterialAndData from, BlockRemapperControl.MaterialAndData to) {
        this.from = from;
        this.to = to;
    }

    public BlockRemapperControl.MaterialAndData getTo() {
        return to;
    }

    public BlockRemapperControl.MaterialAndData getFrom() {
        return from;
    }
}
