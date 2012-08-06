package net.minecraft;

import java.util.ArrayList;

public class MinecraftLauncher
{
	public static String heap = "1024";
	

  public static void main(String[] args)
  
    throws Exception
  {
	  System.out.println("1");
    float heapSizeMegs = (float)(Runtime.getRuntime().maxMemory() / 1024L / 1024L);

    if (heapSizeMegs > 511.0F)
      LauncherFrame.main(args);
    else
      try {
        String pathToJar = MinecraftLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

        ArrayList<String> params = new ArrayList<String>();

        params.add("javaw");
        //if (heap.trim().equals("1024")){
            params.add("-Xms512m");
        	params.add("-Xmx1024m");
        /*}else if (heap.trim().equals("512 MB")){
            params.add("-Xms512m");
        	params.add("-Xmx512m");
        	System.out.println("2");
        }else if (heap.trim().equals("1 GB")){
            params.add("-Xms1024m");
        	params.add("-Xmx1024m");
        	System.out.println("3");
        }else if (heap.trim().equals("2 GB")){
            params.add("-Xms2048m");
        	params.add("-Xmx2048m");
        	System.out.println("4");
        }else if (heap.trim().equals("4 GB")){
            params.add("-Xms4096m");
        	params.add("-Xmx4096m");
        	System.out.println("5");
        }else if (heap.trim().equals("8 GB")){
            params.add("-Xms8192m");
        	params.add("-Xmx8192m");
        	System.out.println("6");
        }else if (heap.trim().equals("16 GB")){
            params.add("-Xms16384m");
        	params.add("-Xmx16384m");
        	System.out.println("7");
        }else{
        	params.add("-Xmx1024m");
        	System.out.println("8");
        }*/
        params.add("-Dsun.java2d.noddraw=true");
        params.add("-Dsun.java2d.d3d=false");
        params.add("-Dsun.java2d.opengl=false");
        params.add("-Dsun.java2d.pmoffscreen=false");
        params.add("-classpath");
        params.add(pathToJar);
        params.add("net.minecraft.LauncherFrame");

        ProcessBuilder pb = new ProcessBuilder(params);
        Process process = pb.start();
        if (process == null) throw new Exception("!");
        System.exit(0);
      } catch (Exception e) {
        e.printStackTrace();
        LauncherFrame.main(args);
      }
  }
}