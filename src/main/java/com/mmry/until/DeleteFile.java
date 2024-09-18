package com.mmry.until;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.javassist.expr.NewExpr;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * @author : 明明如月
 * @date : 2024/4/24 11:04
 */
@Slf4j
public class DeleteFile {
    public static ArrayList<String> failPath;

    public static void main(String[] args) throws IOException {
//        try {
//            deleteAllOfPaths(new String[]{"G:\\xx\\a", "G:\\xx\\a - 副本"});
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        System.out.println(failPath);

        StopWatch watch = new StopWatch();
        watch.start();
        ArrayList<String> list = shearAllOfPaths(new String[]{"G:/mm3/xx"}, "G:/mm3/abc");
        System.out.println(list);

        watch.stop();
        System.out.println(watch.prettyPrint());

//        Files.move(Paths.get("G:\\mm3\\a"),Paths.get("G:\\mm3\\aa"),StandardCopyOption.REPLACE_EXISTING);

    }

    public static ArrayList<String> deleteAllOfPaths(String[] paths) {
        failPath = new ArrayList<>();
        for (String path : paths) {
            boolean flag = true;
            try {
                flag = deleteFileByPath(path);
            } catch (IOException e) {
                failPath.add(path);
            }
            if (!flag)
                failPath.add(path);
        }
        return failPath;
    }

    public static Boolean deleteFileByPath(String path) throws IOException {
        AtomicBoolean flag = new AtomicBoolean(true);
        Path p = Paths.get(path);
        //如果出现AccessDeniedException异常说明是文件夹设置了拒绝访问 需要捕获
        Files.walkFileTree(p,
                new SimpleFileVisitor<Path>() {
                    // 先去遍历删除文件
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                        try {
                            Files.delete(path);
                        } catch (Exception e) {
                            flag.set(false);
                            log.warn("文件删除失败: " + path);
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    // 再去遍历删除目录
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        try {
                            Files.delete(dir);
                        } catch (Exception e) {
                            flag.set(false);
                            log.warn("文件删除失败: " + path);
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        return FileVisitResult.CONTINUE;
                    }
                }
        );
        return flag.get();
    }

    public static Boolean copyFileByPath(Path path, Path targetRoot, int deleteSource) throws IOException {
        AtomicBoolean flag = new AtomicBoolean(true);
        //如果出现AccessDeniedException异常说明是文件夹设置了拒绝访问 需要捕获
        Files.walkFileTree(path,
                new SimpleFileVisitor<Path>() {
                    // 先去遍历删除文件
                    @Override
                    public FileVisitResult visitFile(Path source, BasicFileAttributes attrs) throws IOException {
                        try {

                            String shearPath = source.toString().substring(path.toString().length());
                            Path target = Paths.get(targetRoot.toString() + shearPath);
                            File parentFile = new File(target.toString().substring(0, target.toString().lastIndexOf(File.separator)));
                            if (!parentFile.exists())
                                parentFile.mkdirs();
                            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                            //是否删除原文件
                            if (deleteSource == 1)
                                Files.deleteIfExists(source);

                        } catch (Exception e) {
                            flag.set(false);
                            e.printStackTrace();
                            //如果抛出异常 就不复制了
                            return FileVisitResult.TERMINATE;
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    // 再去遍历删除目录
                    @Override
                    public FileVisitResult postVisitDirectory(Path source, IOException exc) throws IOException {
                        try {
                            String shearPath = source.toString().substring(path.toString().length());
                            Path target = Paths.get(targetRoot.toString() + shearPath);
//                            File parentFile = new File(target.toString().substring(0, target.toString().lastIndexOf(File.separator)));
//                            if (!parentFile.exists())
//                                parentFile.mkdirs();
                            if (!Files.exists(target))
                                Files.createDirectories(target);
                            //是否删除源文件
                            if (deleteSource == 1)
                                Files.deleteIfExists(source);
                        } catch (Exception e) {
                            flag.set(false);
                            e.printStackTrace();
                            return FileVisitResult.TERMINATE;
                        }
                        return FileVisitResult.CONTINUE;
                    }
                }
        );
        return flag.get();
    }

    public static ArrayList<String> copyAllOfPaths(String[] filePath, String target) {
        failPath = new ArrayList<>();
        for (String path : filePath) {
            try {
                Path source = Paths.get(path);
                //如果不是文件夹 文件
                if (!Files.isDirectory(source) && !Files.isRegularFile(source)) {
                    failPath.add(path);
                    continue;
                }
                int index = source.toString().lastIndexOf(File.separator);
                String fileName = source.toString().substring(index + 1);
                Path targetPath = Paths.get(target + File.separator + fileName);
                if (!copyFileByPath(source,targetPath,0)) {
                    if (!failPath.contains(path))
                        failPath.add(path);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (!failPath.contains(path))
                    failPath.add(path);
            }
        }
        return failPath;
    }

    public static ArrayList<String> shearAllOfPaths(String[] filePath, String target) {
        //剪切需要保存一份副本 防备剪切失败
        //获取临时文件夹
        failPath = new ArrayList<>();
        long biggest = 0;//获取最大文件大小
        for (String path : filePath) {
            File file = new File(path);
            if (file.exists()) {
                long size;
                if (file.isFile()) {
                    size = file.length();
                } else
                    size = FileUtils.sizeOfDirectory(new File(path));
                biggest = Math.max(biggest, size);
            } else {
                failPath.add(path);
            }
        }
        Path tempDirectory;
        try {
            tempDirectory = Paths.get(acquireTempCopyPath(biggest));
        } catch (Exception e) {//如果创建临时目录异常就不处理剪切请求了 直接返回
            log.warn("剪切时获取临时拷贝路径错误： " + e.getMessage());
            failPath.clear();
            Arrays.stream(filePath).forEach(f -> {
                failPath.add(f);
            });
            return failPath;
        }
        for (String path : filePath) {
            try {
                Path source = Paths.get(path);
                int index = source.toString().lastIndexOf(File.separator);
                String fileName = source.toString().substring(index + 1);
                if (!Files.exists(Paths.get(target))) Files.createDirectories(Paths.get(target));
                Path targetPath = Paths.get(target + File.separator + fileName);
                Path tempTargetPath = Paths.get(tempDirectory + File.separator + fileName);
                //复制文件副本 预防失败
                //不知道？ 如果复制副本失败那么是否一定会移动文件失败
                if (!Files.exists(source) || !copyFileByPath(source, tempTargetPath, 0))
                    throw new RuntimeException("复制文件副本失败");
                //移动文件
                try {
                    if (!Files.exists(targetPath)) {
//                        copyFileByPathUseSteam(source, targetPath, 0); 使用stream流方式
                        Files.move(source, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        if (!copyFileByPath(source, targetPath, 1)) {
                            if (!failPath.contains(path))
                                failPath.add(path);
                        }
                    }
                    //成功后删除副本文件夹
                    deleteFileByPath(tempTargetPath.toString());
                } catch (IOException e) {
                    if (!failPath.contains(path))
                        failPath.add(path);

                    e.printStackTrace();
                    copyFileByPath(tempTargetPath, source, 0);
                }
            } catch (Exception e) {
                if (!failPath.contains(path))
                    failPath.add(path);
                e.printStackTrace();
            }
        }
        return failPath;
    }

    private static String acquireTempCopyPath(long biggestFile) {
        File[] disks = File.listRoots();
        long biggest = 0;
        File biggestDisk = disks[0];
        for (File file : disks) {
            long usableSpace = file.getUsableSpace();
            if (usableSpace > biggest) {
                biggest = usableSpace;
                biggestDisk = file;
            }
        }
        //三倍大小预留空间
        if (biggest < biggestFile * 3)
            return null;

        return biggestDisk.getAbsolutePath() + "/tempCopyPath";
    }

    private static boolean copyFileByPathUseSteam(Path path, Path targetRoot, int deleteSource) {
        AtomicBoolean flag = new AtomicBoolean(true);
        try {
            Stream<Path> walk = Files.walk(path);
            //倒叙删除 路径最深的先被删除（这样文件会被先删除）
            walk.sorted(Comparator.reverseOrder()).parallel()
                    .forEach(source -> {
                        try {
                            String shearPath = source.toString().substring(path.toString().length());
                            Path target = Paths.get(targetRoot.toString() + shearPath);
                            //如果是文件
                            if (!Files.isDirectory(source)) {
                                File parentFile = new File(target.toString().substring(0, target.toString().lastIndexOf(File.separator)));
                                if (!parentFile.exists())
                                    parentFile.mkdirs();
                                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                                //是否删除原文件
                                if (deleteSource == 1)
                                    Files.deleteIfExists(source);
                            } else {
                                if (!Files.exists(target))
                                    Files.createDirectories(target);
                                //是否删除源文件
                                if (deleteSource == 1)
                                    Files.deleteIfExists(source);
                            }
                        } catch (IOException e) {
                            flag.set(false);
                            e.printStackTrace();
                        }

                    });
        } catch (IOException e) {
            flag.set(false);
            e.printStackTrace();
        }
        return flag.get();
    }

    public void testDeleteFileDir6() throws IOException {
        Path path = Paths.get("G:\\xx\\a");
        try (Stream<Path> walk = Files.walk(path)) {
            //倒叙删除 路径最深的先被删除（这样文件会被先删除）
            walk.sorted(Comparator.reverseOrder()).parallel()
                    .forEach(path1 -> {
                        try {
                            Files.delete(path);
                            System.out.printf("删除文件成功：%s%n", path.toString());
                        } catch (IOException e) {
                            System.err.printf("无法删除的路径 %s%n%s", path, e);
                        }
                    });
        }

    }
}
