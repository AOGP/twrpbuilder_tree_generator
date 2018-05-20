package com.github.twrpbuilder.Interface;

import com.github.twrpbuilder.util.Config;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.LinkedList;

import static com.github.twrpbuilder.MainActivity.rName;

public class Tools implements ToolsInterface {

    public String model;
    public String product;
    public String brand;
    public String codename;
    public String platform;
    public String api;
    public long size;
    public String fingerprint;
    public Config config = null;
    public String out = config.outDir;

    public boolean fexist(String name) {
        if (new File(name).exists())
            return true;
        else
            return false;
    }

    public String command(String run) {
        Process process;
        String o = null;
        StringBuilder sb = null;
        String[] commands = new String[]{"/bin/bash", "-c", run};
        StringBuilder linkedList = new StringBuilder();
        try {
            process = Runtime.getRuntime().exec(commands);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((o = bufferedReader.readLine()) != null) {
                linkedList.append(o.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linkedList.toString();
    }

    public LinkedList command(String run, boolean LinkList) {
        Process process;
        String o = null;
        StringBuilder sb = null;
        String[] commands = new String[]{"/bin/bash", "-c", run};
        LinkedList<String> linkedList = new LinkedList();
        try {
            process = Runtime.getRuntime().exec(commands);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((o = bufferedReader.readLine()) != null) {
                linkedList.add(o.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linkedList;
    }

    public boolean mkdir(String name) {
        File theDir = new File(name).getAbsoluteFile();

        // if the directory does not exist, create it
        if (!theDir.isDirectory()) {
            boolean result = false;

            try{
                theDir.mkdirs();
                result = true;
            }
            catch(SecurityException se){
                System.out.println("Failed to make dir "+name);
                System.exit(0);
            }
            if(result) {
            }
        }else
        {
            System.out.println("Dir: "+name+" already exist");
        }
        return theDir.isDirectory();
    }

    public boolean rm(String name) {
        if (fexist(name)) {
            File file = new File(name);
            if (file.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(file);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (file.isFile()) {
                file.delete();
                return true;
            } else {
                return false;
            }
        } else
            return false;
    }

    public String CopyRight() {
        String copy = "#\n" +
                "# Copyright (C) 2018 The TwrpBuilder Open-Source Project\n" +
                "#\n" +
                "# Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "# you may not use this file except in compliance with the License.\n" +
                "# You may obtain a copy of the License at\n" +
                "#\n" +
                "# http://www.apache.org/licenses/LICENSE-2.0\n" +
                "#\n" +
                "# Unless required by applicable law or agreed to in writing, software\n" +
                "# distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "# See the License for the specific language governing permissions and\n" +
                "# limitations under the License.\n" +
                "# This work is for AOGP Android Open Gionee Project to Build ROM TWRP \n" +
                "# ALGPHELLO khaled necir is the maintainer\n" +
                "# Credit to TwrpBuilder Team\n" +
                "#\n" +
                "\n";
        return copy;
    }

    public void cp(String from, String to) {
        File f = new File(from);
        File t = new File(to);
        if (t.exists()) {
            rm(t.getAbsolutePath());
        }
        try {
            Files.copy(f.toPath(), t.toPath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public String propFile() {
        config = new Config();
        String prop = null;
        if (new File("build.prop").exists()) {
            prop = "build.prop";
        } else if (new File(out + "default.prop").exists()) {
            prop = out + "default.prop";
        } else {
            prop = "null";
        }
        return prop;
    }

    public String getModel() {
        model = command("cat " + propFile() + " | grep -m 1 ro.product.model= | cut -d = -f 2");
        return model;
    }

    public String getBrand() {
        brand = command("cat " + propFile() + " | grep -m 1 ro.product.brand= | cut -d = -f 2").toLowerCase();
        if (brand.contains("-")) {
            String newstr = brand.replace("-", "_");
            return newstr;
        } else if (brand.contains(" ")) {
            String str = brand.replace(" ", "_");
            return str;
        } else {
            return brand;
        }
    }

    public String getCodename() {
        codename =checkData(command("cat " + propFile() + " | grep  -m 1 ro.build.product= | cut -d = -f 2").toLowerCase());
        if (codename.equals(getBrand()))
        {
            return checkData(getModel()).toLowerCase();
        }
        else
        {
            return codename;
        }
    }

    private String checkData(String data){
        if (data.contains("-")) {
            String newstr = data.replace("-", "_");
            return newstr;
        } else if (data.contains(" ")) {
            String str = data.replace(" ", "_");
            return str;
        }
        else if(data.contains("+"))
        {
            String wut=data.replace("+","");
            return wut;
        }
        else {
            return data;
        }
    }

    public String getPlatform() {
        platform = command("cat " + propFile() + " | grep -m 1 ro.board.platform= | cut -d = -f 2");
        if (platform.isEmpty()) {
            platform = command("cat " + propFile() + " | grep -m 1 ro.mediatek.platform= | cut -d = -f 2");
            if (platform.isEmpty()) {
                /*System.out.println("Board platform not defined");
                Clean();
                System.exit(1);*/
                return "generic";
            }
        }
        return platform;
    }

    public String getApi() {
        api = command("cat " + propFile() + " | grep -m 1 ro.product.cpu.abi= | cut -d = -f 2");
        return api;
    }

    public String getFingerPrint() {
        fingerprint = command("cat " + propFile() + " | grep -m 1 ro.build.fingerprint= | cut -d = -f 2");
        return fingerprint;
    }

    public Long getSize() {
        size = new File(Config.recoveryFile).length();
        return size;
    }

    public String getPath() {
        String path = "device" + seprator + getBrand() + seprator + getCodename() + seprator;
        return path;
    }

    public void Write(String name, String data) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileOutputStream(getPath() + name, false));
            writer.println(data);
            writer.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private boolean file(String name) {
        if (new File(name).exists()) {
            rm(name);
            return true;
        } else {
            return false;
        }
    }

    public void Clean() {
        file("build.prop");
        if (rName == null)
            file(Config.recoveryFile);
        file("mounts");
        file("umkbootimg");
        file(Config.outDir);
        file("unpack-MTK.pl");
        file("unpackimg.sh");
        file("bin");
        file("magic");
        file("androidbootimg.magic");

    }

    public void extract(String name) {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        String resourceName = seprator + name;
        try {
            stream = Tools.class.getResourceAsStream(seprator + "asset" + resourceName);
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = System.getProperty("user.dir");
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
            stream.close();
            resStreamOut.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
