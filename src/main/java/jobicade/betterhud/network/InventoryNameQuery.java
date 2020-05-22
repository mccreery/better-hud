package jobicade.betterhud.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import jobicade.betterhud.element.HudElement;

public abstract class InventoryNameQuery implements IMessage {
	private BlockPos pos = null;

	public InventoryNameQuery() {}

	public InventoryNameQuery(BlockPos pos) {
		this.pos = pos;
	}

	public BlockPos getBlockPos() {
		return pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		pos = buffer.readBlockPos();

		fromBytes(buffer);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeBlockPos(pos);

		toBytes(buffer);
	}

	protected void fromBytes(PacketBuffer buf) {}
	protected void toBytes(PacketBuffer buf) {}

	public static class Request extends InventoryNameQuery {
		public Request() {super();}

		public Request(BlockPos pos) {
			super(pos);
		}
	}

	public static class Response extends InventoryNameQuery {
		private ITextComponent name = null;

		public Response() {super();}

		public Response(BlockPos pos, ITextComponent name) {
			super(pos);
			this.name = name;
		}

		public ITextComponent getInventoryName() {
			return name;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {
			try {
				name = buf.readTextComponent();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void toBytes(PacketBuffer buf) {
			buf.writeTextComponent(name);
		}
	}

	public static class ServerHandler implements IMessageHandler<Request, Response> {
		@Override
		public Response onMessage(Request message, MessageContext ctx) {
			//System.out.println("Received block request " + message.getBlockPos());
			TileEntity tileEntity = ctx.getServerHandler().player.world.getTileEntity(message.getBlockPos());

			if (tileEntity instanceof IWorldNameable) {
				return new Response(message.getBlockPos(), tileEntity.getDisplayName());
			} else {
				return null;
			}
		}
	}

	public static class ClientHandler implements IMessageHandler<Response, IMessage> {
		@Override
		public IMessage onMessage(Response message, MessageContext ctx) {
			//System.out.println("Received block response " + message.getBlockPos() + " for name " + message.getInventoryName());

			HudElement.BLOCK_VIEWER.onNameReceived(message.getBlockPos(), message.getInventoryName());
			return null;
		}
	}
}
