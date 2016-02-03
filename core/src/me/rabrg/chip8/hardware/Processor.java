package me.rabrg.chip8.hardware;

import com.badlogic.gdx.Gdx;

import java.util.Random;

import me.rabrg.chip8.CHIP8Emulator;

public final class Processor {

    /**
     * The random instance for opcode CXNN.
     */
    private static final Random RANDOM = new Random();

    /**
     * The font data which is loaded into the beginning of the memory.
     */
    private static final int[] FONT = {
            0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x50, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F
    };

    /**
     * The CPU cycle rate in milliseconds.
     */
    public static final int CYCLE_RATE = 1000 / 500;

    /**
     * The update rate for timers and repainting in milliseconds.
     */
    private static final int UPDATE_RATE = 1000 / 60;

    /**
     * The amount of memory.
     */
    private static final int MEMORY_SIZE = 0xFFF;

    /**
     * The offset of the ROM in memory.
     */
    private static final int ROM_START = 0x200;

    /**
     * The memory of the emulator.
     */
    private final int[] memory = new int[MEMORY_SIZE];

    /**
     * The stack used to store return addresses when subroutines are called.
     */
    private final int[] stack = new int[0x10];

    /**
     * The registers V0 to VF.
     */
    private final int[] register = new int[0x10];

    /**
     * The emulator instance for display and input opcodes.
     */
    private final CHIP8Emulator emulator;

    /**
     * The tick to keep track of timers and display updates.
     */
    private int tick;

    /**
     * The program counter.
     */
    private int pc;

    /**
     * The address pointer.
     */
    private int i;

    /**
     * The stack pointer.
     */
    private int sp;

    /**
     * The sund timer.
     */
    private int st;

    /**
     * The delay timer.
     */
    private int dt;

    /**
     * Constructs a new processor for the specified emulator.
     * @param emulator The emulator.
     */
    public Processor(final CHIP8Emulator emulator) {
        this.emulator = emulator;

        System.arraycopy(FONT, 0, memory, 0, FONT.length);
    }

    /**
     * Executes a processor cycle.
     */
    public void execute() {
        decode(memory[pc++] << 8 | memory[pc++]);
        if (tick == (UPDATE_RATE / CYCLE_RATE)) {
            if (dt > 0) dt--;
            if (st > 0) {
//                if (st == 1)
//                    Toolkit.getDefaultToolkit().beep(); // TODO: beep
                st--;
            }
            emulator.getDisplay().render = true;
            tick = 0;
        }
        tick++;
    }

    /**
     * Loads the ROM at the specified directory.
     * @param dir The directory.
     */
    public void loadROM(final String dir) {
        try {
            for (int i = ROM_START; i < MEMORY_SIZE; i++) {
                memory[i] = 0;
                if (i - ROM_START < 0x10) {
                    stack[i - ROM_START] = 0;
                    register[i - ROM_START] = 0;
                }
            }
            pc = ROM_START;
            i = sp = dt = st = 0;

            final byte[] rom = Gdx.files.internal(dir).readBytes();
            for (int i = 0; i < rom.length; i++)
                memory[i + ROM_START] = rom[i] & 0xFF;
        } catch (final Exception e) {
            System.out.println("Couldn't load ROM " + dir);
        }
    }

    /**
     * Decodes the specified opcode.
     * @param opcode The opcode.
     */
    private void decode(final int opcode) {
        switch (opcode) {
            case 0x00E0:
                emulator.getDisplay().clear();
                return;
            case 0x00EE:
                pc = stack[sp--];
                return;
        }
        switch (opcode & 0xF000) {
            case 0x1000:
                pc = opcode & 0xFFF;
                return;
            case 0x2000:
                stack[++sp] = pc;
                pc = opcode & 0xFFF;
                return;
            case 0x3000:
                if (register[(opcode & 0xF00) >>> 4 * 2] == (opcode & 0xFF))
                    pc += 2;
                return;
            case 0x4000:
                if (register[(opcode & 0xF00) >>> 4 * 2] != (opcode & 0xFF))
                    pc += 2;
                return;
            case 0x6000:
                register[(opcode & 0xF00) >>> 4 * 2] = opcode & 0xFF;
                return;
            case 0x7000:
                int x = ((opcode & 0xF00) >>> 4 * 2);
                register[x] = ((register[x] + (opcode & 0x0FF)) & 0xFF);
                return;
            case 0xA000:
                i = (opcode & 0xFFF);
                return;
            case 0xB000:
                pc = (opcode & 0xFFF) + register[0];
                return;
            case 0xC000:
                register[(opcode & 0xF00) >>> 4 * 2] = ((RANDOM.nextInt(256)) & (opcode & 0x0FF));
                return;
            case 0xD000:
                x = (opcode & 0xF);
                final int[] sprite = new int[x];
                for (int j = i, count = 0; j < i + x; j++, count++)
                    sprite[count] = memory[j];
                final boolean erased = emulator.getDisplay().draw(register[(opcode & 0xF00) >>> 4 * 2],
                        register[(opcode & 0xF0) >>> 4], sprite);
                register[0xF] = (erased ? 1 : 0);
                return;
        }
        switch (opcode & 0xF00F) {
            case 0x5000:
                if (register[(opcode & 0xF00) >>> 4 * 2] == register[(opcode & 0xF0) >>> 4])
                    pc += 2;
                return;
            case 0x8000:
                register[(opcode & 0xF00) >>> 4 * 2] = register[(opcode & 0xF0) >>> 4];
                return;
            case 0x8001:
                int x = ((opcode & 0xF00) >>> 4 * 2);
                register[x] = (register[x] | register[(opcode & 0xF0) >>> 4]);
                return;
            case 0x8002:
                x = ((opcode & 0xF00) >>> 4 * 2);
                register[x] = (register[x] & register[(opcode & 0x0F0) >>> 4]);
                return;
            case 0x8003:
                x = ((opcode & 0xF00) >>> 4 * 2);
                register[x] = (register[x] ^ register[(opcode & 0xF0) >>> 4]);
                return;
            case 0x8004:
                x = ((opcode & 0xF00) >>> 4 * 2);
                int sum = register[x] + register[(opcode & 0xF0) >>> 4];
                register[0xF] = sum > 0xFF ? 1 : 0;
                register[x] = (sum & 0xFF);
                return;
            case 0x8005:
                x = ((opcode & 0xF00) >>> 4 * 2);
                int y = ((opcode & 0xF0) >>> 4);
                register[0xF] = register[x] > register[y] ? 1 : 0;
                register[x] = (register[x] - register[y]) & 0xFF;
                return;
            case 0x8006:
                x = ((opcode & 0xF00) >>> 4 * 2);
                register[0xF] = (register[x] & 0x1) == 1 ? 1 : 0;
                register[x] = (register[x] >>> 1);
                return;
            case 0x8007:
                x = ((opcode & 0xF00) >>> 4 * 2);
                y = ((opcode & 0xF0) >>> 4);
                register[0xF] = register[y] > register[x] ? 1 : 0;
                register[x] = ((register[y] - register[x]) & 0xFF);
                return;
            case 0x800E:
                x = ((opcode & 0xF00) >>> (4 * 2));
                register[0xF] = (register[x] >>> 7) == 0x1 ? 1 : 0;
                register[x] = ((register[x] << 1) & 0xFF);
                return;
            case 0x9000:
                if (register[(opcode & 0xF00) >>> (4 * 2)] != register[(opcode & 0xF0) >>> 4])
                    pc += 2;
                return;
        }
        switch (opcode & 0xF0FF) {
            case 0xE09E:
                if (emulator.getKeyboard().isKeyPressed((register[(opcode & 0xF00) >>> (4 * 2)])))
                    pc += 2;
                return;
            case 0xE0A1:
                if (!emulator.getKeyboard().isKeyPressed((register[(opcode & 0xF00) >>> (4 * 2)])))
                    pc += 2;
                return;
            case 0xF007:
                register[(opcode & 0xF00) >>> 4 * 2] = (dt & 0xFF);
                return;
            case 0xF00A:
                emulator.getKeyboard().depress();
                while (!emulator.getKeyboard().isPressed()) ; // TODO: make not shit
                for (int j = 0, x = (opcode & 0xF00) >>> 4 * 2; j <= 0xF; j++)
                    if (emulator.getKeyboard().isKeyPressed(j)) {
                        register[x] = j;
                        return;
                    }
                return;
            case 0xF015:
                dt = register[(opcode & 0xF00) >>> 4 * 2];
                return;
            case 0xF018:
                st = register[(opcode & 0xF00) >>> 4 * 2];
                return;
            case 0xF01E:
                i = (i + register[(opcode & 0xF00) >>> 4 * 2]) & 0xFFF;
                return;
            case 0xF029:
                i = register[(opcode & 0xF00) >>> 4 * 2] * 5;
                return;
            case 0xF033:
                int x = ((opcode & 0xF00) >>> 4 * 2);
                memory[i] = (register[x] / 100);
                memory[i + 1] = ((register[x] - memory[i]) / 10);
                memory[i + 2] = (register[x] - memory[i] - memory[i + 1]);
                return;
            case 0xF055:
                System.arraycopy(register, 0, memory, i, ((opcode & 0xF00) >>> 4 * 2) + 1);
                return;
            case 0xF065:
                System.arraycopy(memory, i, register, 0, ((opcode & 0xF00) >>> 4 * 2) + 1);
        }
    }
}
