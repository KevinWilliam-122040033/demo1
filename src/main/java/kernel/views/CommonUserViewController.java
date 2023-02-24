package kernel.views;

//import com.sun.tools.corba.se.idl.Util;
//import com.sun.tools.corba.se.idl.constExpr.Or;
//import com.sun.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import kernel.Main;
import kernel.Session;
import kernel.models.CommonUser;
import kernel.models.Magazine;
import kernel.models.Order;
import kernel.utils.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommonUserViewController {
    @FXML
    private Main mainApp;
    @FXML
    private BorderPane subscriptionPane;
    @FXML
    private BorderPane personInfoPane;
    @FXML
    private BorderPane subscriptedPane;
    @FXML
    private TableView<Magazine> magazineTable;
    @FXML
    private TableColumn<Magazine, String> nameColumn;
    @FXML
    private TableColumn<Magazine, String> priceColumn;
    @FXML
    private TableColumn<Magazine, String> classNameColumn;
    @FXML
    private ImageView magazineCover;
    @FXML
    private Label magazineNameLabel;
    @FXML
    private Label magazineIdLabel;
    @FXML
    private Label magazineOfficeLabel;
    @FXML
    private Label magazineCycleLabel;
    @FXML
    private Label magazinePriceLabel;
    @FXML
    private Label magazinIntroLabel;
    @FXML
    private Label magazinClassLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private ChoiceBox<String> timeChoiceBox;
    @FXML
    private Label totalPrice;
    @FXML
    private Button confirmButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField trueNameField;
    @FXML
    private TextField sidField;
    @FXML
    private TextField telField;
    @FXML
    private TextField addrField;
    @FXML
    private Button changeButton;
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, Integer> orderIdColumn;
    @FXML
    private TableColumn<Order, Integer> magazineIdColumn;
    @FXML
    private TableColumn<Order, String> magazineNameColumn;
    @FXML
    private TableColumn<Order, Integer> copiesNumColumn;
    @FXML
    private TableColumn<Order, Integer> monthColumn;
    @FXML
    private TableColumn<Order, Integer> totalPriceColumn;
    @FXML
    private PieChart statisticPriceChart;
    @FXML
    private Rectangle imageBorder;
    @FXML
    private ChoiceBox<String> copiesNumChoiceBox;


    private ObservableList<Magazine> magazinesData = FXCollections.observableArrayList();
    private ObservableList<Order> orderData = FXCollections.observableArrayList();

    public CommonUserViewController() {
    }

    public Main getMainApp() {
        return mainApp;
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * 初始化列表的数据绑定和函数监听,在load时会执行这些操作。
     * 这个时候mainApp还未被设置，所以才是null
     * 这个函数，会在构造函数结束后执行
     *
     * Initialize the data bonding and function listener, executed when loading
     * At this time mainApp is not set up, so it will be null
     * this function will be executed after calling construction function
     */
    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        classNameColumn.setCellValueFactory(cellData -> cellData.getValue().classNameProperty());
        initTable();
        initChoiceBox();
        initCopiesNumChoiceBox();
        /*
         *==================================================
         *                 !!important!!
         *       监听器要放到后面，用来避免未初始化的监听
         *       listener should be put after to avoid un-initialized listeners
         *==================================================
         */
        magazineTable.getSelectionModel().selectedItemProperty().addListener(
                ((observable, oldValue, newValue) -> updateDetail(newValue))
        );
        timeChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateTotalPrice()
        );
        copiesNumChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateTotalPrice()
        );
        magazineTable.getSelectionModel().selectFirst();
        /*
         *==================================================
         *                 !!important!!
         *     这里mainApp还没有载入，所以不能使用，一直为null
         *     at this point mainApp is not loaded, cannot be used. always be null
         *==================================================
         */
        initUserNameLabel();
    }

    public void initUserNameLabel() {
        if (Session.getType().equals("commonUser")) {
            userNameLabel.setText(Session.getUsername());
        } else if (Session.getType().equals("admin")) {
            userNameLabel.setText("Precious Mr./Mrs./Ms." + Session.getUsername());
        }
    }

    /**
     * 初始化杂志显示列表
     * initialize magazine lists
     */
    public void initTable() {
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        String sql = "select a.id, a.coverPath, a.`name`, a.office, a.cycle, a.price, a.intro, b.className from `magazine` a, `mClass` b where a.classNumber = b.id;";
        List<Object> params = new ArrayList<>();
        try {
            List<Magazine> rawData = jdbcUtils.findMoreProResult(sql, params, Magazine.class);
            magazinesData.addAll(rawData);
            magazineTable.setItems(magazinesData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新右侧的详细信息的展示界面
     * update righthand side ddetail infomation panel
     */
    public void updateDetail(Magazine solo) {
        System.out.println("右侧信息已被更新");
        //依次更新
        Magazine selected = magazineTable.getSelectionModel().getSelectedItem();
//        File newFile = new File("src/kernel/statics/images/" + selected.getCoverPath());
        File newFile = new File("src/main/resources/kernel/statics/images/" + selected.getCoverPath());
        /*
         *==================================================
         *                 !!important!!
         *     cannot use relative path
         *     ref：https://blog.csdn.net/major_out/article/details/66971188?utm_source=debugrun&utm_medium=referral
         *==================================================
         */
        magazineCover.setImage(new Image(newFile.getAbsoluteFile().toURI().toString()));
        UIAdjistUtils.adjustImageBorder(magazineCover, imageBorder);
        magazineIdLabel.setText("Magazine Id：" + selected.getId());
        magazineNameLabel.setText("Magazine name：" + selected.getName());
        magazineOfficeLabel.setText("Publisher：" + selected.getOffice());
        magazineCycleLabel.setText("Cycle：" + selected.getCycle());
        magazinePriceLabel.setText("Price per month：" + selected.getPrice());
        magazinIntroLabel.setText("Intro：" + selected.getIntro());
        magazinClassLabel.setText("Category：" + selected.getClassName());
        updateTotalPrice();
    }

    /**
     * 初始化月份选择盒子中的可选项
     * initialize month selection box
     */
    public void initChoiceBox() {
        ObservableList<String> choiceItems = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) {
            choiceItems.add(i-1, Integer.toString(i));
        }
        timeChoiceBox.setItems(choiceItems);
        timeChoiceBox.setValue("1");
    }

    public void initCopiesNumChoiceBox() {
        ObservableList<String> choiceItems = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) {
            choiceItems.add(i-1, Integer.toString(i));
        }
        copiesNumChoiceBox.setItems(choiceItems);
        copiesNumChoiceBox.setValue("1");
    }

    /**
     * 更新实时的商品价格
     * update real-time prices
     */
    private void updateTotalPrice() {
        System.out.println("total price updated");
        int month = Integer.parseInt(timeChoiceBox.getValue());
        int price = Integer.parseInt(magazineTable.getSelectionModel().getSelectedItem().getPrice());
        int copiesNum = Integer.parseInt(copiesNumChoiceBox.getValue());
        totalPrice.setText("total price：" +  price*month*copiesNum + "$");
    }


    /**
     * 跳转到报刊订阅界面
     * jump to magazine subscription page
     */
    @FXML
    private void handleSubscription() {
        subscriptedPane.setVisible(false);
        personInfoPane.setVisible(false);
        subscriptionPane.setVisible(true);
    }

    /**
     * 跳转到个人信息修改界面
     * jump to personal info modification page
     */
    @FXML
    private void handlePersonInfo() {
        subscriptionPane.setVisible(false);
        subscriptedPane.setVisible(false);
        personInfoPane.setVisible(true);
        initPersonInfoPane();
    }

    /**
     * 跳转到已订阅界面
     * jump to subscription page
     */
    @FXML
    private void handleSubscripted() {
        personInfoPane.setVisible(false);
        subscriptionPane.setVisible(false);

        // 初始化订单详情页面,这里对数字进行处理要加上.asObject但是暂时不知道为什么
        orderIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        magazineIdColumn.setCellValueFactory(cellData -> cellData.getValue().midProperty().asObject());
        magazineNameColumn.setCellValueFactory(cellData -> cellData.getValue().magazineNameProperty());
        copiesNumColumn.setCellValueFactory(cellData -> cellData.getValue().copiesNumProperty().asObject());
        monthColumn.setCellValueFactory(cellData -> cellData.getValue().monthProperty().asObject());
        totalPriceColumn.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty().asObject());

        // 获取该用户的相关订单信息
        //obtain user's subscription info
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        String sql = "select a.id, b.id as uid, b.username as userName, c.id as mid, c.`name` as magazineName, a.`month`, a.copiesNum, a.totalPrice from `order` a, commonUser b, magazine c where a.uid = b.id and a.mid = c.id and b.id = ?;";
        List<Object> params = new ArrayList<>();
        params.add(Session.getId());

        try {
            List<Order> orders = jdbcUtils.findMoreProResult(sql, params, Order.class);
            orderData.clear();
            orderData.addAll(orders);
            orderTable.setItems(orderData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        statisticPriceChart.setData(StatisticHelper.makeNameOrderData());

        // 可视化改该界面
        // visualize this page
        subscriptedPane.setVisible(true);
    }

    /**
     * 用户退出，返回登录界面
     * user quits, back to login page
     */
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("views/LoginView.fxml"));
            Parent loginView = loader.load();
            LoginViewController loginViewController = loader.getController();
            mainApp.getPrimaryStage().setScene(new Scene(loginView, 1152, 640));
            mainApp.getPrimaryStage().show();
            loginViewController.setMainApp(mainApp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向数据库提交订阅表单
     * submit form to DB
     */
    @FXML
    private void handleConfirm() {
        if (!DialogUtils.confirm(mainApp.getPrimaryStage(), "please make sure the order is correct, it cannot be altered once placed")) {
            return;
        }
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        String sql = "insert into `order` (uid, mid, month, copiesNum, totalPrice) values(?, ?, ?, ?, ?)";
        List<Object> params = new ArrayList<>();
        params.add(Session.getId());
        params.add(magazineTable.getSelectionModel().getSelectedItem().getId());
        params.add(Integer.parseInt(timeChoiceBox.getValue()));
        params.add(Integer.parseInt(copiesNumChoiceBox.getValue()));
        int month = Integer.parseInt(timeChoiceBox.getValue());
        int price = Integer.parseInt(magazineTable.getSelectionModel().getSelectedItem().getPrice());
        int copiesNum = Integer.parseInt(copiesNumChoiceBox.getValue());
        params.add(month*price*copiesNum);

        try {
            jdbcUtils.updateByPreparedStatement(sql, params);
            DialogUtils.good(mainApp.getPrimaryStage(), "order saved");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向数据库提交改变个人信息的表单
     * submit personal info modification form to DB
     */
    @FXML
    private void handleChange() {
        String errorMessage = InputChecker.userUpdateCheck(userNameLabel.getText(),
                                                               usernameField.getText(),
                                                               passwordField.getText(),
                                                               trueNameField.getText(),
                                                               sidField.getText(),
                                                               telField.getText(),
                                                               addrField.getText());
        if (errorMessage == null) {
            JdbcUtils jdbcUtils = new JdbcUtils();
            jdbcUtils.getConnection();
            String sql = "update `commonUser` set username = ?, password = ?, trueName = ?, " +
                    "sid = ?, tel = ?, address = ? where id = ?";
            List<Object> params = new ArrayList<>();

            // 添加更改后的新值
            params.add(usernameField.getText());
            params.add(passwordField.getText());
            params.add(trueNameField.getText());
            params.add(sidField.getText());
            params.add(telField.getText());
            params.add(addrField.getText());
            params.add(Session.getId());
            try {
                jdbcUtils.updateByPreparedStatement(sql, params);
                DialogUtils.good(mainApp.getPrimaryStage(), "updated");
                    Session.setUsername(usernameField.getText());
                    initUserNameLabel();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            DialogUtils.tips(mainApp.getPrimaryStage(), errorMessage);
        }
    }


    public void initPersonInfoPane() {
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        String sql = "select * from `commonUser` where username = ?";
        List<Object> params = new ArrayList<>();
        params.add(Session.getUsername());

        try {
            CommonUser userNow = jdbcUtils.findSimpleProResult(sql, params, CommonUser.class);

            // 设置显示对应的原始数据
            // setting original data for display
            usernameField.setText(userNow.getUsername());
            passwordField.setText(userNow.getPassword());
            trueNameField.setText(userNow.getTrueName());
            sidField.setText(userNow.getSid());
            telField.setText(userNow.getTel());
            addrField.setText(userNow.getAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClassOrderPie() {
        statisticPriceChart.setData(StatisticHelper.makeClassOrderData());
    }

    @FXML
    private void handleNameOrderPie() {
        statisticPriceChart.setData(StatisticHelper.makeNameOrderData());
    }

    @FXML
    private void handleClassPricePie() {
        statisticPriceChart.setData(StatisticHelper.makeClassPriceData(Session.getUsername()));
    }

    @FXML
    private void handleNamePricePie() {
        statisticPriceChart.setData(StatisticHelper.makeNamePriceData(Session.getUsername()));
    }
}
