package com.mmry.until;

import org.junit.Test;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

@Component
public class FilePath {

    @Test
    public void t1(){
        File[] roots = File.listRoots();
        for (File root : roots) {
            System.out.println(root);
        }

        System.out.println(new File("G:\\autorun.inf").isHidden());
        System.out.println(new File("G:\\Download").isFile());
        System.out.println(new File("G:\\第七特区.txt").isDirectory());


        File file = new File("G:/");
        Arrays.stream(file.listFiles()).forEach(s -> System.out.println(s));

        file = roots[0];
        Arrays.stream(file.list()).forEach(s -> System.out.println(s));
     }


}
