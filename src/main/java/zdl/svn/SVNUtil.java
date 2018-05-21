package zdl.svn;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.*;

/**
 * @author ZDLegend
 * @create 2017/12/11
 */

public class SVNUtil {

    /**
     * 获取所有下载文件条目
     */
    public static List<String> displayRepositoryTree() throws SVNException {
        List<String> list = new ArrayList<>();
        listEntries(SVNApplication.repository, "", list);
        return list;
    }

    /**
     * 获取文件相关信息
     */
    public static JSONObject displayFile(String filePath) throws SVNException {

        JSONObject data = new JSONObject();

        SVNRepository repository = SVNApplication.repository;

        //此变量用来存放要查看的文件的属性名/属性值列表。
        SVNProperties fileProperties = new SVNProperties();
        //此输出流用来存放要查看的文件的内容。
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //获得版本库中文件的类型状态（是否存在、是目录还是文件），参数-1表示是版本库中的最新版本。
        SVNNodeKind nodeKind = repository.checkPath(filePath, -1);
        if (nodeKind == SVNNodeKind.NONE) {
            System.out.println("要查看的文件在 '" + SVNApplication.path + "'中不存在.");
        } else if (nodeKind == SVNNodeKind.DIR) {
            System.out.println("要查看对应版本的条目在 '" + SVNApplication.path + "'中是一个目录.");
        }

        //获取要查看文件的内容和属性，结果保存在baos和fileProperties变量中。
        repository.getFile(filePath, -1, fileProperties, baos);

        data.put("committed-date", fileProperties.getStringValue("svn:entry:committed-date"));
        data.put("last-author", "svn:entry:last-author");

        List<SVNLogEntry> entries = new ArrayList<>();
        repository.log(new String[]{filePath},//为过滤的文件路径前缀，为空表示不进行过滤
                entries,
                0,//-1代表最新的版本号，初始版本号为0
                -1,
                true,
                true);

        data.put("logNum", entries.size());
        JSONArray revisionInfoList = new JSONArray();
        for (SVNLogEntry entry : entries) {
            JSONObject revisionInfo = new JSONObject();
            revisionInfo.put("author", entry.getAuthor());
            revisionInfo.put("revision", entry.getRevision());
            revisionInfo.put("message", entry.getMessage());
            revisionInfo.put("date", entry.getDate());
            revisionInfoList.add(revisionInfo);
        }

        data.put("log", revisionInfoList);
        return data;
    }

    /**
     * 获取文件相关信息
     */
    public static List<String> displayVersions(String filePath) throws SVNException {

        List<String> list = new ArrayList<>();

        SVNRepository repository = SVNApplication.repository;

        //获得版本库中文件的类型状态（是否存在、是目录还是文件），参数-1表示是版本库中的最新版本。
        SVNNodeKind nodeKind;

        nodeKind = repository.checkPath(filePath, -1);

        if (nodeKind == SVNNodeKind.NONE) {
            System.out.println("要查看的文件在 '" + SVNApplication.path + "'中不存在.");
        } else if (nodeKind == SVNNodeKind.DIR) {
            System.out.println("要查看对应版本的条目在 '" + SVNApplication.path + "'中是一个目录.");
        }

        List<SVNLogEntry> entries = new ArrayList<>();

        repository.log(new String[]{filePath},//为过滤的文件路径前缀，为空表示不进行过滤
                entries,
                0,//-1代表最新的版本号，初始版本号为0
                -1,
                true,
                true);

        for (SVNLogEntry entry : entries) {
            list.add(Long.toString(entry.getRevision()));
        }
        return list;
    }

    /**
     * 获取文件流
     */
    public static void getFile(String filePath, long revision, OutputStream cos) throws SVNException {
        SVNRepository repository = SVNApplication.repository;
        SVNProperties fileProperties = new SVNProperties();
        repository.getFile(filePath, revision, fileProperties, cos);
    }

    /**
     * 获取最新版本文件流
     */
    public static void getFile(String filePath, OutputStream cos) throws SVNException {
        getFile(filePath, -1, cos);
    }

    /**
     * 更新文件
     */
    public static long doUpdate(String path) {
        SVNClientManager clientManager = SVNApplication.clientManager;

        //要更新的文件
        File updateFile = new File(SVNApplication.path + File.separator + path);
        //获得updateClient的实例
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);
        //执行更新操作
        long versionNum = 0;
        try {
            versionNum = updateClient.doUpdate(updateFile, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
        } catch (SVNException e) {
            System.out.println("目录:" + SVNApplication.path + File.separator + path + " update 失败");
            e.printStackTrace();
        }

        return versionNum;
    }

    /**
     * 增加/修改文件
     */
    public synchronized static long doCommit(String filePath, String message) throws SVNException {
        SVNClientManager clientManager = SVNApplication.clientManager;

        //要提交的文件
        File commitFile = new File(filePath);

        //获取此文件的状态（是文件做了修改还是新添加的文件？）
        SVNStatus status = clientManager.getStatusClient().doStatus(commitFile, true);
        if (status == null) {
            //把此文件增加到版本库中
            clientManager.getWCClient().doAdd(commitFile, false, false, false,
                    SVNDepth.INFINITY, false, false);
        }

        //提交此文件
        SVNCommitInfo info = clientManager.getCommitClient().doCommit(
                new File[]{commitFile}, true, message, null,
                null, true, false, SVNDepth.INFINITY);

        Long res = info.getNewRevision();
        if (res == -1L) {
            return SVNApplication.repository.getLatestRevision();
        }

        return info.getNewRevision();
    }

    public static void doDelete(String... strings) throws SVNException {
        SVNClientManager clientManager = SVNApplication.clientManager;
        SVNURL deleteUrls[] = new SVNURL[strings.length];
        for (int i = 0; i < strings.length; i++) {
            SVNURL repositoryOptUrl = SVNURL.parseURIEncoded(strings[i]);
            deleteUrls[i] = repositoryOptUrl;
        }
        clientManager.getCommitClient().doDelete(deleteUrls, "从SVN数据库中删除多余文件");
    }

    public static void doDelete(String url) throws SVNException {
        SVNNodeKind nodeKind = SVNApplication.repository.checkPath(url, -1);
        if (nodeKind != SVNNodeKind.NONE) {
            System.out.println("delete svn : '" + SVNApplication.path + "/" + url + "'");
            SVNClientManager clientManager = SVNApplication.clientManager;
            SVNURL repositoryOptUrl = SVNURL.parseURIEncoded(SVNApplication.path + "/" + url);
            SVNURL deleteUrls[] = new SVNURL[1];
            deleteUrls[0] = repositoryOptUrl;
            clientManager.getCommitClient().doDelete(deleteUrls, "从SVN数据库中删除文件 : " + url);
        } else {
            System.out.println("URL : '" + SVNApplication.path + "/" + url + "' does not exist");
        }
    }

    /**
     * 获取文件
     */
    public static void checkOut(String filePath, long revision) throws SVNException {

        SVNClientManager clientManager = SVNApplication.clientManager;

        //要提交的文件
        File commitFile = new File(SVNApplication.path + File.separator + filePath);

        //通过客户端管理类获得updateClient类的实例。
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);

        //执行check out 操作，返回工作副本的版本号。
        long workingVersion;
        if (revision == -1L) {
            workingVersion = updateClient
                    .doCheckout(SVNApplication.repositoryURL, commitFile, SVNRevision.HEAD, SVNRevision.HEAD,
                            SVNDepth.INFINITY, false);
        } else {
            SVNRevision svnRevision = SVNRevision.create(revision);
            workingVersion = updateClient
                    .doCheckout(SVNApplication.repositoryURL, commitFile, svnRevision, svnRevision,
                            SVNDepth.INFINITY, false);
        }

        System.out.println("把版本：" + workingVersion + " check out 到目录：" + commitFile + "中。");
    }

    /**
     * 获取文件
     */
    public static void checkOut(String filePath, String url) throws SVNException {

        SVNClientManager clientManager = SVNApplication.clientManager;
        SVNURL repositoryURL = SVNURL.parseURIEncoded(SVNApplication.path + url);

        File commitFile = new File(filePath);

        //通过客户端管理类获得updateClient类的实例。
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);

        //执行check out 操作，返回工作副本的版本号。
        long workingVersion = updateClient
                .doCheckout(repositoryURL, commitFile, SVNRevision.HEAD, SVNRevision.HEAD,
                        SVNDepth.INFINITY, false);
        System.out.println("把版本：" + workingVersion + " check out 到目录：" + commitFile + "中。");
    }

    public static void doMkDir(String filePath, String message) throws SVNException {
        String path = SVNApplication.path + "/" + filePath;
        SVNNodeKind nodeKind = SVNApplication.repository.checkPath(filePath, -1);
        if (nodeKind == SVNNodeKind.NONE) {
            SVNApplication.clientManager.getCommitClient().doMkDir(
                    new SVNURL[]{SVNURL.parseURIEncoded(path)}, message);
        }
    }

    public static boolean doImport(String filePath, String url, String message) throws SVNException {
        String path = SVNApplication.path + "/" + url;
        SVNNodeKind nodeKind = SVNApplication.repository.checkPath(url, -1);
        if (nodeKind == SVNNodeKind.NONE) {
            SVNApplication.clientManager.getCommitClient().doImport(
                    new File(filePath), SVNURL.parseURIEncoded(path), message, null,
                    false, false, SVNDepth.INFINITY);
            return true;
        }
        return false;
    }

    //建立随机文件夹
    public static String createTemporaryFile() {
        String temporary = UUID.randomUUID().toString();
        createDir(SVNApplication.path + temporary);
        return temporary;
    }

    // 创建目录
    private static boolean createDir(String destDirName) {
        File dir = new File(destDirName);

        // 判断目录是否存在
        if (dir.exists()) {
            //System.out.println("创建目录失败，目标目录已存在！");
            return false;
        }

        // 创建目标目录
        if (dir.mkdirs()) {
            //System.out.println("创建目录成功！" + destDirName);
            return true;
        } else {
            System.out.println("创建目录失败:" + destDirName);
            return false;
        }
    }

    /**
     * 此函数递归的获取版本库中某一目录下的所有条目。
     */
    private static void listEntries(SVNRepository repository, String path, List<String> tree) throws SVNException {

        //获取版本库的path目录下的所有条目。参数－1表示是最新版本。
        Collection entries = repository.getDir(path, -1, null,
                (Collection) null);
        for (Object entry1 : entries) {
            SVNDirEntry entry = (SVNDirEntry) entry1;
            System.out.println("/" + (path.equals("") ? "" : path + "/")
                    + entry.getName() + " (author: '" + entry.getAuthor()
                    + "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")");
            /*
             * 检查此条目是否为目录，如果为目录递归执行。如果不是则放入JSON数组中
             */
            if (entry.getKind() == SVNNodeKind.DIR) {
                listEntries(repository, (path.equals("")) ? entry.getName()
                        : path + "/" + entry.getName(), tree);
            } else {
                tree.add("/" + (path.equals("") ? "" : path + "/") + entry.getName());
            }
        }
    }
}
