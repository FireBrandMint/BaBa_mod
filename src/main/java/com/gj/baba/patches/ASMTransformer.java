package com.gj.baba.patches;

import com.gj.baba.BaBa;
import com.gj.baba.patches.mixins.NaturalRegenPatch;
import com.gj.baba.patches.hooks.ShieldPatch;
import com.gj.baba.patches.util.ASMHelper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.Arrays;

public class ASMTransformer implements IClassTransformer
{
    static final String[] classesToTransform =
    {
            "net.minecraft.item.ItemShield",
            "net.minecraft.util.FoodStats"
    };

    @Override
    public byte[] transform(String name, String transformedName, byte[] classBeingTransformed) {
        boolean isObfuscated = !name.equals(transformedName);
        int index = Arrays.asList(classesToTransform).indexOf(transformedName);
        return index != -1 ? transformForReal(index, classBeingTransformed, isObfuscated) : classBeingTransformed;
    }

    public static byte[] transformForReal(int index, byte[] classBeingTransformed, boolean isObfuscated)
    {
        BaBa.logger.atWarn().log("Patching: " + classesToTransform[index] + " babais");
        try
        {
            ClassNode classNode = new ClassNode(Opcodes.ASM5);
            ClassReader classReader = new ClassReader(classBeingTransformed);
            classReader.accept(classNode, 0);

            switch (index)
            {
                case 0:
                    patchShield(classNode, isObfuscated);
                    break;
                case 1:
                    patchNaturalHealing(classNode, isObfuscated);
            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            BaBa.logger.atInfo().log("Successfully patched: " + classesToTransform[index] + " babais");
        }

        return classBeingTransformed;
    }

    static void patchShield(ClassNode subject, boolean isObfuscated)
    {
        String methodName = isObfuscated ? "a" : "onItemRightClick";
        String descriptor = ASMHelper.toMethodDescriptor(isObfuscated ? "uc" : "net/minecraft/util/ActionResult", isObfuscated? "ams" : "net/minecraft/world/World", isObfuscated ? "aeb" : "net/minecraft/entity/player/EntityPlayer", isObfuscated ? "tz" : "net/minecraft/util/EnumHand");

        boolean success = false;
        for(MethodNode method : subject.methods)
        {
            AbstractInsnNode target = null;
            if(method.name.equals(methodName) && method.desc.equals(descriptor))
            {
                for(AbstractInsnNode instruction : method.instructions.toArray())
                {
                    if(instruction.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) instruction).var == 2 && instruction.getNext().getNext().getOpcode() == Opcodes.INVOKEVIRTUAL)
                    {
                        target = instruction;
                    }
                }
            }

            if(target != null)
            {
                success = true;
                LabelNode newLabelNode = new LabelNode();

                InsnList toInsert = new InsnList();
                //toInsert.add(new FieldInsnNode(Opcodes.GETSTATIC, "ShieldPatch", "enabled", "I"));
                //toInsert.add(new JumpInsnNode(Opcodes.IFEQ, newLabelNode));
                toInsert.add(new TypeInsnNode(Opcodes.NEW, isObfuscated ? "uc" : "net/minecraft/util/ActionResult"));
                toInsert.add(new InsnNode(Opcodes.DUP));
                toInsert.add(new VarInsnNode(Opcodes.ALOAD, 1));
                toInsert.add(new VarInsnNode(Opcodes.ALOAD, 2));
                toInsert.add(new VarInsnNode(Opcodes.ALOAD, 3));
                toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(ShieldPatch.class), "onItemRightClick", descriptor, false));
                toInsert.add(new InsnNode(Opcodes.ARETURN));
                //toInsert.add(newLabelNode);
                method.instructions.insert(toInsert);
                break;
            }
        }

        if(!success) BaBa.logger.atInfo().log("Failed to patch class " + subject.name + " isObfuscated: " + isObfuscated);

        //ItemShield.class.getTypeName()
    }

    static void patchNaturalHealing(ClassNode subject, boolean isObfuscated)
    {
        String methodName = isObfuscated ? "a" : "onUpdate";
        String descriptor = "(" + ASMHelper.toDescriptor(isObfuscated? "aed" : "EntityPlayer") + ")V";

        for(MethodNode method : subject.methods) {
            AbstractInsnNode target = null;
            if (method.name.equals(methodName) && method.desc.equals(descriptor)) {
                for(AbstractInsnNode instruction : method.instructions.toArray())
                {
                    if(instruction.getOpcode() == Opcodes.ISTORE && ((VarInsnNode) instruction).var == 3)
                    {
                        target = instruction;
                    }
                }
            }
            if(target != null)
            {
                InsnList toInsert = new InsnList();
                toInsert.add(new VarInsnNode(Opcodes.ALOAD, 3));
                toInsert.add(new VarInsnNode(Opcodes.ALOAD, 1));
                toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(NaturalRegenPatch.class), "onUpdate", isObfuscated? "(I;Laed;)V" : "(I;LEntityPlayer;)V"));
                toInsert.add(new InsnNode(Opcodes.RETURN));

                method.instructions.insert(target, toInsert);



                break;
            }
        }


    }
}
