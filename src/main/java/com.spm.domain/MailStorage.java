package com.spm.domain;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 这是一个持久层的模块，用于访问和管理储存在磁盘上的邮件记录。
 * 使用文件开关读写实现。
 *
 * @author 张哲源
 */
public class MailStorage {

    private static final String lineSep = System.getProperty("line.separator");
    private static final String sep = "``";
    private static final Pattern liniPattern = Pattern.compile("([~`])");
    private static final Pattern parsePattern = Pattern.compile("~([~`])");
    private final String storageFilename;
    private List<MailRec> recList;

    /**
     * @param filename 传入想要使用MailStorage操作的文件路径
     *                 文件内每条记录顺序为:uid from sub tsp filename，记录内使用分割符分割内容
     *                 <p>
     *                 该方法用于读取文件内记录并且在内存中以MailRec的列表形式保存
     *                 </p>
     */
    public MailStorage(String filename) {
        recList = new ArrayList<>();
        storageFilename = filename;
        File storageFile = new File(storageFilename);
        try {
            //如果文件不存在则创造文件
            if (!storageFile.exists()) {
                storageFile.createNewFile();
            }

            Reader reader = new FileReader(storageFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            while (line != null) {
                //将一条记录放入mailRecList中
                String split[] = parse(line);
                MailRec mailRec = new MailRec();
                mailRec.setUid(split[0]);
                mailRec.setFrom(split[1]);
                mailRec.setSub(split[2]);
                mailRec.setTsp(split[3]);
                mailRec.setFilename(split[4]);
                recList.add(mailRec);

                line = bufferedReader.readLine();
            }

            bufferedReader.close();
            reader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    /**
     * @param rec 想要向文件和列表内添加的MailRec对象
     *            <p>
     *            该方法会将 rec 的 uid，from，sub，tsp，filename 以文件的形式持久化存储
     *            并且将传入对象添加到列表中
     *            </p>
     */
    public synchronized void add(MailRec rec) throws IOException {
        //将对象存入文件
        Writer writer = new FileWriter(storageFilename, true);
        writer.write(linify(rec.getUid(), rec.getFrom(), rec.getSub(), rec.getTsp(), rec.getFilename()) + lineSep);
        writer.close();
        //将传入对象加入列表
        recList.add(rec);
    }

    /**
     * @param rec 传入的 rec 应该是之前使用 add()添加的某个 MailRec 对象
     *            <p>
     *            删除该对象对应在文件中的数据，从内存的记录列表中移除该邮件记录，
     *            即 getRecList()返回的列表对象中不再包含该对象。
     *            </p>
     */
    public synchronized void del(MailRec rec) throws IOException {
        Reader reader = new FileReader(storageFilename);
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<String> lineList = new ArrayList<>();

        //从持久记录中删除传入对象-读取
        String line = bufferedReader.readLine();
        while (line != null) {
            //使用分隔符拆分一行记录
            String split[] = parse(line);
            //根据UID判断,是要删除的则不动，不是则将其放入lineList
            if (!rec.getUid().equals(split[0])) {
                lineList.add(line);
            }
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        reader.close();

        //从持久记录中删除传入对象-重写
        Writer writer = new FileWriter(storageFilename);
        for (String lines : lineList) {
            writer.write(lines + lineSep);
        }
        writer.close();

        //从列表中删除传入对象
        recList.remove(rec);
    }

    /**
     * @return 返回到调用时刻为止的记录列表。
     * <p>
     * 这个列表可 以随意操作，不会对邮件记录集（MailStorage）产生影响
     * </p>
     */
    public synchronized List<MailRec> getRecList() {
        return new ArrayList<>(recList);
    }

    /**
     * Generate one line record in the storage.
     *
     * @return The line record.
     * @author BoxCatGarden
     */
    private String linify(String uid, String from, String sub, String tsp, String filename) {
        return liniPattern.matcher(uid).replaceAll("~$1") + sep +
                liniPattern.matcher(from).replaceAll("~$1") + sep +
                liniPattern.matcher(sub).replaceAll("~$1") + sep +
                liniPattern.matcher(tsp).replaceAll("~$1") + sep +
                liniPattern.matcher(filename).replaceAll("~$1");
    }

    /**
     * Parse a line record into several fields.
     *
     * @return The result fields.
     * @author BoxCatGarden
     */
    private String[] parse(String line) {
        String[] fields = line.split(sep);
        for (int i = 0; i < fields.length; ++i) {
            fields[i] = parsePattern.matcher(fields[i]).replaceAll("$1");
        }
        return fields;
    }
}