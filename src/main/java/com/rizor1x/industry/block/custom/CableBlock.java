package com.rizor1x.industry.block.custom;

import com.rizor1x.industry.block.entity.CableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.Map;

public class CableBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    // Свойства для 6 сторон (Подключен ли провод туда?)
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    // Массив свойств для удобства
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = Map.of(
            Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH,
            Direction.WEST, WEST, Direction.UP, UP, Direction.DOWN, DOWN
    );

    // ХИТБОКСЫ (6х6 пикселей в центре блока)
    private static final VoxelShape CORE = Block.box(5, 5, 5, 11, 11, 11);
    private static final VoxelShape UP_ARM = Block.box(5, 11, 5, 11, 16, 11);
    private static final VoxelShape DOWN_ARM = Block.box(5, 0, 5, 11, 5, 11);
    private static final VoxelShape NORTH_ARM = Block.box(5, 5, 0, 11, 11, 5);
    private static final VoxelShape SOUTH_ARM = Block.box(5, 5, 11, 11, 11, 16);
    private static final VoxelShape WEST_ARM = Block.box(0, 5, 5, 5, 11, 11);
    private static final VoxelShape EAST_ARM = Block.box(11, 5, 5, 16, 11, 11);

    // Собираем форму провода из кусочков в зависимости от подключений
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;
        if (state.getValue(UP)) shape = Shapes.or(shape, UP_ARM);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, DOWN_ARM);
        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_ARM);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_ARM);
        if (state.getValue(WEST)) shape = Shapes.or(shape, WEST_ARM);
        if (state.getValue(EAST)) shape = Shapes.or(shape, EAST_ARM);
        return shape;
    }

    // Проверяем, есть ли рядом блок с энергией при установке
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return makeConnections(context.getLevel(), context.getClickedPos());
    }

    // Обновляем форму провода, если рядом поставили/сломали машину
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (level instanceof Level realLevel) {
            boolean canConnect = canConnectTo(realLevel, currentPos, direction);
            return state.setValue(PROPERTY_BY_DIRECTION.get(direction), canConnect);
        }
        return state;
    }

    private BlockState makeConnections(Level level, BlockPos pos) {
        BlockState state = this.defaultBlockState();
        for (Direction dir : Direction.values()) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), canConnectTo(level, pos, dir));
        }
        return state;
    }

    // МАГИЯ: Соединяемся только если у соседа есть Energy Capability!
    private boolean canConnectTo(Level level, BlockPos pos, Direction dir) {
        BlockPos neighborPos = pos.relative(dir);
        var energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
        return energyStorage != null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    // Добавляем переменную тира
    private final CableTier tier;

    // Изменяем конструктор
    public CableBlock(Properties properties, CableTier tier) {
        super(properties);
        this.tier = tier;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false)
                .setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
    }

    public CableTier getTier() {
        return tier;
    }

    // Передаем тир в BlockEntity
    @javax.annotation.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(pos, state);
    }

    @javax.annotation.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl, pos, st, be) -> { if (be instanceof CableBlockEntity cable) cable.tick(); };
    }
}