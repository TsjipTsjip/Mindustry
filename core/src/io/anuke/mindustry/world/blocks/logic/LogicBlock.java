package io.anuke.mindustry.world.blocks.logic;

import io.anuke.arc.*;
import io.anuke.arc.graphics.*;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.util.*;
import io.anuke.mindustry.entities.traits.*;
import io.anuke.mindustry.entities.traits.BuilderTrait.*;
import io.anuke.mindustry.entities.type.*;
import io.anuke.mindustry.graphics.*;
import io.anuke.mindustry.ui.*;
import io.anuke.mindustry.world.*;
import io.anuke.mindustry.world.meta.*;

import java.io.*;

public abstract class LogicBlock extends Block{

    public LogicBlock(String name){
        super(name);
        rotate = true;
        group = BlockGroup.logic;
        update = true;
        entityType = LogicEntity::new;
        controllable = false;
    }

    @Override
    public void update(Tile tile){
        LogicEntity entity = tile.entity();
        entity.signal = signal(tile);
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.add("signal", entity -> new Bar(
        () -> Core.bundle.format("block.signal", Integer.toBinaryString(((LogicEntity)entity).signal).replace("1", "[accent]1").replace("0", "[lightgray]0")),
        () -> Color.clear,
        () -> 0));
    }

    @Override
    public void draw(Tile tile){
        LogicEntity entity = tile.entity();
        Draw.rect("logic-base", tile.drawx(), tile.drawy());

        Draw.color(entity.signal > 0 ? Pal.accent : Color.white);
        super.draw(tile);
        Draw.color();
    }

    @Override
    public TextureRegion[] generateIcons(){
        return new TextureRegion[]{Core.atlas.find("logic-base"), Core.atlas.find(name)};
    }

    @Override
    public void drawRequestRegion(BuilderTrait.BuildRequest req, Eachable<BuildRequest> list) {
        TextureRegion back = Core.atlas.find("logic-base");
        Draw.rect(back, req.drawx(), req.drawy(),
        back.getWidth() * req.animScale * Draw.scl,
        back.getHeight() * req.animScale * Draw.scl,
        0);
        Draw.rect(region, req.drawx(), req.drawy(),
        region.getWidth() * req.animScale * Draw.scl,
        region.getHeight() * req.animScale * Draw.scl,
        !rotate ? 0 : req.rotation * 90);
    }

    public int getSignal(Tile tile){
        if(tile == null || !(tile.block() instanceof LogicBlock)) return 0;
        return tile.<LogicEntity>entity().signal;
    }

    public int sfront(Tile tile){
        return getSignal(tile.front());
    }

    public int sback(Tile tile){
        return getSignal(tile.back());
    }

    public int sleft(Tile tile){
        return getSignal(tile.left());
    }

    public int sright(Tile tile){
        return getSignal(tile.right());
    }

    /** @return signal to send next frame. */
    public abstract int signal(Tile tile);

    public class LogicEntity extends TileEntity{
        public int signal;

        @Override
        public void write(DataOutput stream) throws IOException{
            super.write(stream);
            stream.writeInt(signal);
        }

        @Override
        public void read(DataInput stream, byte revision) throws IOException{
            super.read(stream, revision);
            signal = stream.readInt();
        }
    }
}
