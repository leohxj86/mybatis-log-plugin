package mybatis.log.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import mybatis.log.action.gui.FilterSetting;
import mybatis.log.tail.TailContentExecutor;
import mybatis.log.util.ConfigUtil;


/**
 * 插件入口
 *
 * @author ob
 */
public class ShowLogInConsoleAction extends DumbAwareAction {

    public ShowLogInConsoleAction(Project project) {
        super();
        ConfigUtil.active = true;
        ConfigUtil.init(project);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) return;
    }

    public void showLogInConsole(final Project project) {
        if (project == null) return;
        ConfigUtil.setRunning(project, true);
        final TailContentExecutor executor = new TailContentExecutor(project);
        Disposer.register(project, executor);
        executor.withRerun(() -> showLogInConsole(project));
        executor.withStop(() -> {
            ConfigUtil.setRunning(project, false);
            ConfigUtil.setIndexNum(project, 1);
        }, () -> ConfigUtil.getRunning(project));
        executor.withFilter(() -> {
            //启动filter配置
            FilterSetting dialog = new FilterSetting(project);
            dialog.pack();
            dialog.setSize(600, 400);//配置大小
            dialog.setLocationRelativeTo(null);//位置居中显示
            dialog.setVisible(true);
        });
        executor.withActivateToolWindow(true);
        executor.run();
    }

}
