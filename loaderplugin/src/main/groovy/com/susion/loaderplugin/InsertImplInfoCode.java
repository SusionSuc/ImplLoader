package com.susion.loaderplugin;

import com.android.SdkConstants;
import com.susion.annotation2.ProtocolConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * Created by susion on 2018/10/29.
 * 向 ImplLoader.init 中插入方法
 */
public class InsertImplInfoCode {

    public static List<String> getImplInfoClassesFromJar(File file) throws IOException {
        List<String> implInfoClassesName = new ArrayList<>();
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (name.endsWith(SdkConstants.DOT_CLASS) && name.startsWith(ProtocolConstants.IMPL_INFO_GENERATE_PATH)) {
                String className = trimName(name, 0).replace('/', '.');
                //class 名字应去除后缀
                System.out.println("get impl info class from jar :" + className);
                implInfoClassesName.add(className);
            }
        }
        return implInfoClassesName;
    }

    /**
     * [prefix]com/xxx/aaa.class --> com/xxx/aaa
     * [prefix]com\xxx\aaa.class --> com\xxx\aaa
     */
    private static String trimName(String s, int start) {
        return s.substring(start, s.length() - SdkConstants.DOT_CLASS.length());
    }

    public static List<String> getImplInfoClassesFromDir(File dir) {
        List<String> implInfoClassesName = new ArrayList<>();
        File packageDir = new File(dir, ProtocolConstants.IMPL_INFO_GENERATE_PATH);

        if (packageDir.exists() && packageDir.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(packageDir,
                    new SuffixFileFilter(SdkConstants.DOT_CLASS, IOCase.INSENSITIVE), TrueFileFilter.INSTANCE);

            for (File f : files) {
                String className = trimName(f.getAbsolutePath(), dir.getAbsolutePath().length() + 1).replace(File.separatorChar, '.');
                System.out.println("get impl info class from dir :" + className);
                implInfoClassesName.add(className);
            }
        }

        return implInfoClassesName;
    }

//    public static void insertImplInfoInitMethod(File file, String absolutePath) throws IOException {
//        File outFile = new File(file.getParent(), file.getName() + ".opt");
//        if (outFile.exists())
//            outFile.delete();
//
//        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outFile));
//
//        JarFile jarFile = new JarFile(file);
//        Enumeration<JarEntry> entries = jarFile.entries();
//
//        while (entries.hasMoreElements()) {
//            JarEntry jarEntry = entries.nextElement();
//            String entryName = jarEntry.getName();
//            ZipEntry zipEntry = new ZipEntry(entryName);
//            InputStream inputStream = jarFile.getInputStream(jarEntry);
//            jarOutputStream.putNextEntry(zipEntry);
//            //扫描到 ImplLoader 类
//            if (entryName.endsWith(SdkConstants.DOT_CLASS) && entryName.startsWith(ProtocolConstants.ASM_IMPL_LOADER_CLASS_PATH)) {
//                byte[] bytes = getInsertMethodByte(inputStream);
//                jarOutputStream.write(bytes);
//            } else {
//                jarOutputStream.write(IOUtils.toByteArray(inputStream));
//            }
//            inputStream.close();
//            jarOutputStream.closeEntry();
//        }
//    }
//
//    private static byte[] getInsertMethodByte(InputStream inputStream) throws IOException {
//        ClassReader cr = new ClassReader(inputStream);
//        ClassWriter cw = new ClassWriter(cr, 0);
//        ClassVisitor cv = new MyClassVisitor(Opcodes.ASM5, cw);
//        cr.accept(cv, ClassReader.EXPAND_FRAMES);
//        return cw.toByteArray();
//    }
//
//    static class MyClassVisitor extends ClassVisitor {
//
//        MyClassVisitor(int api, ClassVisitor cv) {
//            super(api, cv);
//        }
//
//        public void visit(int version, int access, String name, String signature,
//                          String superName, String[] interfaces) {
//            super.visit(version, access, name, signature, superName, interfaces);
//        }
//
//        @Override
//        public MethodVisitor visitMethod(int access, String name, String desc,
//                                         String signature, String[] exceptions) {
//            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
//            //generate code into this method
//            if (name == ProtocolConstants.IMPL_INFO_CLASS_INIT_METHOD) {
//                mv = new RouteMethodVisitor(Opcodes.ASM5, mv);
//            }
//            return mv;
//        }
//    }
//
//    static class RouteMethodVisitor extends MethodVisitor {
//
//        RouteMethodVisitor(int api, MethodVisitor mv) {
//            super(api, mv);
//        }
//
//        @Override
//        public void visitInsn(int opcode) {
//            //generate code before return
//            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
//                extension.classList.each {
//                    name ->
//                            name = name.replaceAll("/", ".")
//                    mv.visitLdcInsn(name)//类名
//                    // generate invoke register method into LogisticsCenter.loadRouterMap()
//                    mv.visitMethodInsn(Opcodes.INVOKESTATIC
//                            , ScanSetting.GENERATE_TO_CLASS_NAME
//                            , ScanSetting.REGISTER_METHOD_NAME
//                            , "(Ljava/lang/String;)V"
//                            , false)
//                }
//            }
//            super.visitInsn(opcode);
//        }
//
//        @Override
//        public void visitMaxs(int maxStack, int maxLocals) {
//            super.visitMaxs(maxStack + 4, maxLocals);
//        }
//    }


    // 新产生一个类
    public static void insertImplInfoInitMethod(Set<String> implInfoClasses, String outputDirPath) {

        if (implInfoClasses.isEmpty()) return;

        System.out.println("开始修改 ImplLoader.init() 方法 :");

        try {
            long ms = System.currentTimeMillis();

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, writer) {
            };
            String className = ProtocolConstants.IMPL_LOADER_HELP_CLASS.replace('.', '/');
            System.out.println("目标生成的Class的名字  :" + className);

            cv.visit(50, Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", null);

            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                    ProtocolConstants.IMPL_LOADER_HELP_INIT_METHOD, "()V", null, null);

            mv.visitCode();

            for (String clazz : implInfoClasses) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, clazz.replace('.', '/'),
                        ProtocolConstants.IMPL_INFO_CLASS_INIT_METHOD,
                        "()V",
                        false);
                System.out.println(" 插入 " + clazz + " init 方法");
            }

            mv.visitMaxs(0, 0);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitEnd();
            cv.visitEnd();

            File dest = new File(outputDirPath, className + SdkConstants.DOT_CLASS);
            dest.getParentFile().mkdirs();
            new FileOutputStream(dest).write(writer.toByteArray());

            System.out.println("成功生成  :" + className);

        } catch (IOException e) {
        }

    }

}
