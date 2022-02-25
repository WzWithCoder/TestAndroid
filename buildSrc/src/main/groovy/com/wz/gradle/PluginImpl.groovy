package com.wz.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.wz.gradle.spi.IServiceProvider
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by wangzheng on 2017/2/17.
 * https://github.com/allenymt/PrivacySentry/
 * https://segmentfault.com/a/1190000009001892
 * http://blog.csdn.net/sbsujjbcy/article/details/50782830
 * http://blog.csdn.net/u010386612/article/details/51131642
 * https://blog.csdn.net/yulong0809/article/details/77752098
 * https://www.jianshu.com/p/37df81365edf
 * https://github.com/dengshiwei/asm-module/blob/master/doc/blog/AOP%20利器%20ASM%20基础入门.md
 * https://www.jianshu.com/p/11ae9bf7425a
 */

public class PluginImpl implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        println "start test-plugin"

        project.getExtensions().create("exceptionConfig", ExpConfig.class)
        if (project.plugins.hasPlugin(AppPlugin)) {
            def android = project.extensions.findByType(AppExtension)
            android.registerTransform(new ExpTransformer(project))
        }

        ServiceLoader<IServiceProvider> serviceLoader = ServiceLoader.load(IServiceProvider.class)
        Iterator<IServiceProvider> iterator = serviceLoader.iterator()
        while (iterator.hasNext()) {
            IServiceProvider serviceProvider = iterator.next()
            serviceProvider.invoke("====ImplServiceProvider====")
        }

        //loadAgent(project)

        // 创建清理任务
        //Task cleanTask = project.tasks.create("test_task", TestCleanTask)
        // 执行完lint后，再执行
        //cleanTask.dependsOn project.tasks.getByName('lint')
    }


//    private void loadAgent(Project project) {
//
//        Logger logger = project.getLogger();
//
//        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
//
//        int p = nameOfRunningVM.indexOf('@');
//        String pid = nameOfRunningVM.substring(0, p);
//
//
//        logger.info("[WZAgent] Dynamically loading  Android Agent instrumentation...");
//        String jarFilePath;
//        try {
//            jarFilePath = TestAgent.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().toString();
//
//            jarFilePath = new File(jarFilePath).getCanonicalPath();
//
//            logger.info("[WZAgent] Found  Android Agent instrumentation within " + jarFilePath);
//        } catch (URISyntaxException e) {
//            logger.error("[WZAgent] Unable to find  Android Agent instrumentation jar");
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            logger.error("[WZAgent] Unable to find  Android Agent instrumentation jar");
//            throw new RuntimeException(e);
//        }
//        try {
//            VirtualMachine vm = VirtualMachine.attach(pid);
//            vm.loadAgent(jarFilePath/*, System.getProperty("WZAgent.AgentArgs")*/);
//            vm.detach();
//        } catch (Exception e) {
//            logger.error("[WZAgent] Error encountered while loading the NetworkBench NewLens Android agent", e);
//            throw new RuntimeException(e);
//        }
//    }
}
