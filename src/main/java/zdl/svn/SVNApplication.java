package zdl.svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @author ZDLegend
 * @create 2018/5/18
 */

public class SVNApplication {

    private static String url = "https://192.0.0.241/zdl";
    private static String name = "zdlegend";
    private static String password = "password";

    public static String path = "F:\\SVN_CODE";

    //根据URL实例化SVN版本库
    public static SVNRepository repository;

    //SVN的URL
    public static SVNURL repositoryURL;

    //	声明SVN客户端管理类
    public static SVNClientManager clientManager;

    public static void init(){
        /*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();

        /*
         * For using over svn:// and svn+xxx://
         */
        SVNRepositoryFactoryImpl.setup();

        /*
         * For using over file:///
         */
        FSRepositoryFactory.setup();

        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        clientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, name, new String(password));

        try {
            //获取SVN的URL。
            repositoryURL = SVNURL.parseURIEncoded(url);
            //根据URL实例化SVN版本库。
            //repository = SVNRepositoryFactory.create(repositoryURL);
            repository = DAVRepositoryFactory.create(repositoryURL , null);
        } catch (SVNException svne) {
            /*
             * 打印版本库实例创建失败的异常。
             */
            System.out.println("创建版本库实例时失败，版本库的URL是 '"
                    + url + "': " + svne.getMessage());
        }

        /*
         * 对版本库设置认证信息。
         */
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
        repository.setAuthenticationManager(authManager);
    }

    public static void main(String ... a){
        init();

        try {
            System.out.println(SVNUtil.displayRepositoryTree());
        } catch (SVNException e) {
            e.printStackTrace();
        }
    }
}
