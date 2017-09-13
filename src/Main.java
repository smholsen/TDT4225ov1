import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.EnumSet;


import static java.nio.file.Files.newByteChannel;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class Main {

    private static final int BLOCKSIZE = 4096;

    public static void main(String[] args){
        // testWrite();
        testRead();
    }

    private static void testRead() {
        System.out.println(String.format("%-15s", "Size in GB") +
                String.format("%-15s", "Throughput") +
                String.format("%-15s", "Milliseconds")+
                String.format("%-15s", "Seconds"));
        readAndLog("myjavadata1GB");
        readAndLog("myjavadata3GB");
        readAndLog("myjavadata8GB");
    }

    private static void testWrite(){
        System.out.println(String.format("%-15s", "Size in GB") +
                String.format("%-15s", "Throughput") +
                String.format("%-15s", "Milliseconds")+
                String.format("%-15s", "Seconds"));
        //writeAndLog(1);
        //writeAndLog(2);
        //writeAndLog(4);
        //writeAndLog(8);
        //writeAndLog(16);
    }

    private static void readAndLog(String filename){
        File file = new File(System.getProperty("user.dir"), filename);
        ByteBuffer buff = ByteBuffer.allocate(BLOCKSIZE);
        try {
            InputStream in = new FileInputStream(file);
            int bytesRead = 0;
            long tStart = System.currentTimeMillis();
            while (in.available() > 0) {
                bytesRead = in.read(buff.array());
                buff.clear();
            }
            // End the timer
            long tEnd = System.currentTimeMillis();
            // Calculate timedifference in milliseconds
            long tDelta = tEnd - tStart;
            System.out.println(String.format("%-15s", file.length() / 1024 / 1024 / 1024) +
                    String.format("%-15s", getThroughtput(file.length(), tDelta) + " MB/s") +
                    String.format("%-15s", tDelta)+
                    String.format("%-15s", (float) tDelta/1000));
            } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static void  writeAndLog(int GB){
        long blocksEaGB = (1024 * 1024 * 1024) / BLOCKSIZE;
        // I use buffers of 1 GB.
        int tmpGB = GB;
        long totalBytes = (long) GB * 1024 * 1024 * 1024;
        Path file = Paths.get(System.getProperty("user.dir"), "myjavadata8GB");
        SeekableByteChannel out;
        try {

            out = newByteChannel(file, EnumSet.of(CREATE, APPEND));

            // Start the timer
            long tStart = System.currentTimeMillis();
            while (tmpGB > 0){
                for (int i = 0; i < blocksEaGB ; i++) {
                    ByteBuffer buff = ByteBuffer.allocate(BLOCKSIZE);
                    out.write(buff);
                }
                tmpGB -= 1;
            }
            // End the timer
            long tEnd = System.currentTimeMillis();
            // Calculate timedifference in milliseconds
            long tDelta = tEnd - tStart;

            System.out.println(String.format("%-15s", GB) +
                    String.format("%-15s", getThroughtput(totalBytes, tDelta) + " MB/s") +
                    String.format("%-15s", tDelta)+
                    String.format("%-15s", (float) tDelta/1000));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static float getThroughtput(long bytesWritten, long tDelta) {
        // Returns MB/s
        float seconds = (float) tDelta/1000;
        float throughput =  ((bytesWritten/1024)/1024)/ seconds;
        // Format
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Float.valueOf(decimalFormat.format(throughput));
    }
}
