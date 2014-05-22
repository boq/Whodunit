package openmods.whodunit.injection;

import openmods.whodunit.data.CallCollector;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodInjector extends MethodVisitor {

    private static final Type CALL_TARGET = Type.getType(CallCollector.class);

    private final int location;

    public MethodInjector(MethodVisitor mv, int location) {
        super(Opcodes.ASM4, mv);
        this.location = location;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        super.visitLdcInsn(location);
        super.visitMethodInsn(Opcodes.INVOKESTATIC, CALL_TARGET.getInternalName(), "registerCall", "(I)V");
    }

}
