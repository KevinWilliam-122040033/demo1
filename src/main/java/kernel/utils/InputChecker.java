package kernel.utils;

import javafx.beans.property.StringProperty;
import kernel.models.*;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InputChecker {
    public static String commonUserInfoCheck(String uname, String pass, String tname, String sid, String tel, String addr) {
        String errorMessage = null;

        //检查地址
        //check address
        if (addr.length() > 60) {
            errorMessage = "address too long";
        }

        // 检查电话
        //check tel number
        if (tel.length() > 60) {
            errorMessage = "tel too long";
        }

        //检查身份证号
        //check id
        // TODO: 2018/6/24 这里以后会改成用正则匹配身份证号
        // TODO: make it to match regular expression
        if (sid.length() > 60) {
            errorMessage = "ID too long";
        }

        //检查真实姓名
        //check real name
        if (tname.length() > 60) {
            errorMessage = "Real name too long";
        }

        //密码是不是符合要求
        //check whether password matches requirements
        if (pass.length() == 0) {
            errorMessage = "Empty pswd";
        } else if (pass.length() > 60) {
            errorMessage = "Pswd too long";
        }

        //用户名是不是符合要求
        //check whether username matches requirements
        if (uname.length() == 0) {
            errorMessage = "Empty Usrname";
        } else if (uname.length() > 60) {
            errorMessage = "Usrname too long";
        }

        return errorMessage;
    }

    public static String commonUserSignUpCheck(String uname, String pass, String tname, String sid, String tel, String addr) {
        String errorMessage = commonUserInfoCheck(uname, pass, tname, sid, tel, addr);

        //检查该用户是否已被注册
        //check if username already used
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        String sql = "select * from `commonUser` where username = ?";
        List<Object> params = new ArrayList<>();
        params.add(uname);

        try {
            CommonUser sameUser = jdbcUtils.findSimpleProResult(sql, params, CommonUser.class);
            if (sameUser != null) {
                errorMessage = "Usrname already registered";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return errorMessage;
    }

    public static String userUpdateCheck(String olduname,String uname, String pass, String tname, String sid, String tel, String addr) {
        String errorMessage = commonUserInfoCheck(uname, pass, tname, sid, tel, addr);

        //检查该用户是否已被注册
        //check if the username is used
        if (!olduname.equals(uname)) {
            JdbcUtils jdbcUtils = new JdbcUtils();
            jdbcUtils.getConnection();
            String sql = "select * from `commonUser` where username = ?";
            List<Object> params = new ArrayList<>();
            params.add(uname);

            try {
                CommonUser sameUser = jdbcUtils.findSimpleProResult(sql, params, CommonUser.class);
                if (sameUser != null) {
                    errorMessage = "Usrname already used";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return errorMessage;
    }

    public static String commonUserSignUpCheck(String id, String uname, String pass, String tname, String sid, String tel, String addr) {
        String errorMessage = commonUserSignUpCheck(uname, pass, tname, sid, tel, addr);

        //在原来的基础上在检查id是否已经被注册
        //based on other conditions, check if id is used
        JdbcUtils jdbcUtils= new JdbcUtils();
        jdbcUtils.getConnection();
        String sql = "select * from `commonUser` where id = ?";
        List<Object> params = new ArrayList<>();

        if (id == null || id.length() == 0) {
            return errorMessage = "empty id";
        } else if(id.length() > 11) {
            return  errorMessage = "entered id too long";
        } else {
            try {
                Integer.parseInt(id);
            } catch (Exception e) {
                return errorMessage = "id contains only numbers";
            }
        }

        params.add(Integer.parseInt(id));

        try {
            CommonUser sameUser = jdbcUtils.findSimpleProResult(sql, params, CommonUser.class);
            if (sameUser != null) {
                errorMessage = "Id already registered";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorMessage;
    }

    public static String commonUserUpdateCheck(CommonUser oldInfo, CommonUser newInfo) {
        String errorMessage = commonUserInfoCheck(newInfo.getUsername(), newInfo.getPassword(), newInfo.getTrueName(), newInfo.getSid(), newInfo.getTel(), newInfo.getAddress());

        //如果没有改动id和用户名这两个不能重复的信息
        //check if id and username is altered
        if (oldInfo.getId() != newInfo.getId()) {
            errorMessage = "cannot alter user's id";
        }

        if (!oldInfo.getUsername().equals(newInfo.getUsername())) {
            JdbcUtils jdbcUtils = new JdbcUtils();
            jdbcUtils.getConnection();
            String sql = "select * from `commonUser` where username = ?";
            List<Object> params = new ArrayList<>();
            params.add(newInfo.getUsername());
            try {
                CommonUser same = jdbcUtils.findSimpleProResult(sql, params, CommonUser.class);
                if (same != null) {
                    errorMessage = "usrname already used";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return errorMessage;
    }


    public static String adminSignUpCheck(String id, String uname, String pass) {
        String errorMessage = null;

        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        String sql = "select * from `admin` where id = ?";
        List<Object> params = new ArrayList<>();

        if (id == null || id.length() == 0) {
            return errorMessage = "empty id";
        } else if(id.length() > 11) {
            return  errorMessage = "id too long";
        } else {
            try {
                Integer.parseInt(id);
            } catch (Exception e) {
                return errorMessage = "id contains only numbers";
            }
        }

        if (uname == null || uname.length() == 0) {
            return errorMessage = "empty username";
        }


        params.add(Integer.parseInt(id));

        try {
            Admin sameUser = jdbcUtils.findSimpleProResult(sql, params, Admin.class);
            if (sameUser != null) {
                errorMessage = "Id already used";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sql = "select * from `admin` where username = ?";
        params.clear();
        params.add(uname);

        try {
            Admin sameUser = jdbcUtils.findSimpleProResult(sql, params, Admin.class);
            if (sameUser != null) {
                errorMessage = "username already used";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pass.length() > 60) {
            errorMessage = "password too long";
        } else if (pass.length() == 0) {
            errorMessage = "please enter password";
        }

        return errorMessage;
    }

    public static String adminInfoUpdateCheck(Admin oldInfo, Admin newInfo) {
        String errorMessage = null;

        //如果没有改动id和用户名这两个不能重复的信息
        //check if id and username are altered
        if (oldInfo.getId() != newInfo.getId()) {
            errorMessage = "cannot alter user's ID";
        }

        if (newInfo.getUsername() == null || newInfo.getUsername().length() == 0) {
            return errorMessage = "empty username";
        }

        if (newInfo.getPassword() == null || newInfo.getPassword().length() == 0) {
            return errorMessage = "empty password";
        }


        if (!oldInfo.getUsername().equals(newInfo.getUsername())) {
            JdbcUtils jdbcUtils = new JdbcUtils();
            jdbcUtils.getConnection();
            String sql = "select * from `admin` where username = ?";
            List<Object> params = new ArrayList<>();
            params.add(newInfo.getUsername());
            try {
                Admin same = jdbcUtils.findSimpleProResult(sql, params, Admin.class);
                if (same != null) {
                    errorMessage = "username already used";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return errorMessage;
    }

    public static String magazineSignUpCheck(String id, File image, String mname, String offic, String cycle, String price, String mclas, String intor) {
        String errorMessage = magazineRegularCheck(id, mname, offic, cycle, price, mclas, intor);

        //检查ID, 图片, 杂志名重复.
        //check id, image and magazine name duplications
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        String sql = "select id, coverPath, name from `magazine`";
        List<Object> params = new ArrayList<>();
        try {
            List<Map<String, Object>> infos = jdbcUtils.findModeResult(sql, params);
            for (Map<String, Object> info : infos) {
//                System.out.println(image.getName() + "  " + info.get("coverPath"));
                /*
                 *==================================================
                 *                 !!important!!
                 *     字符串比较一定要用equals这里就是一个很好的例子
                 *     use equals to compare strings
                 *==================================================
                 */
                if (image == null || image.getName().equals(info.get("coverPath"))) {
                    errorMessage = "image already used, select a new one";
                }
                if (mname.equals(info.get("name"))) {
                    errorMessage = "magazine name already used";
                }
                if (id == null || id.length() == 0) {
                    return errorMessage = "empty id";
                } else {
                    try {
                        Integer.parseInt(id);
                    } catch (Exception e) {
                        return errorMessage = "id contains only numbers";
                    }
                }
                if (Integer.parseInt(id) == (Integer) info.get("id")) {
                    errorMessage = "id already registered";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return errorMessage;
    }

    public static String magazineUpdateCheck(Magazine oldInfo, Magazine newInfo) {
        String errorMessage = magazineRegularCheck(Integer.toString(newInfo.getId()), newInfo.getName(), newInfo.getOffice(), newInfo.getCycle(), newInfo.getPrice(), newInfo.getClassName(), newInfo.getIntro());
        //id不允许被修改
        //id is not allowed to be altered
        if (oldInfo.getId() != newInfo.getId()) {
            errorMessage = "cannot alter magazine's ID";
        }

        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
        List<Object> params = new ArrayList<>();

        // 检查改后的用户名
        //check altered username
        if (!oldInfo.getName().equals(newInfo.getName())) {
            String sql = "select * from `magazine` where name = ?";
            params.add(newInfo.getName());
            try {
                Map<String, Object> same = jdbcUtils.findSimpleResult(sql, params);
                /*
                 *==================================================
                 *                 !!important!!
                 *     字典要判断空不空，而不是null
                 *     check if the dictionary(Map) is empty
                 *==================================================
                 */
                if (!same.isEmpty()) {
                    errorMessage = "magazine name used";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //检查改后的图片是否重复
        //check if the altered image duplicates
        if (!oldInfo.getCoverPath().equals(newInfo.getCoverPath())) {
            String sql = "select coverPath from `magazine`";
            params.clear();
            try {
                List<Map<String, Object>> infos = jdbcUtils.findModeResult(sql, params);
                for (Map<String, Object> info : infos) {
                    if (newInfo.getCoverPath().equals(info.get("coverPath"))) {
                        errorMessage = "image already used, select a new one";
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return errorMessage;
    }

    public static String magazineRegularCheck(String id, String mname, String offic, String cycle, String price, String mclas, String intor) {
        String errorMessage = null;
        System.out.println("checking magazine");
        // 检查常规项
        // common checkers
        if (offic.length() == 0) {
            errorMessage = "empty publisher";
        }
        if (cycle.length() == 0) {
            errorMessage = "empty cycle";
        }
        if (price.length() == 0) {
            errorMessage = "empty price";
        }
        if (mclas.length() == 0) {
            errorMessage = "no category";
        }
        if (intor.length() > 140) {
            errorMessage = "description too long";
        }
        if (id.length() > 11) {
            errorMessage = "id too long";
        }
        if (mname.length() > 60) {
            errorMessage = "username too long";
        }
        return errorMessage;
    }

    public static String orderSignUpCheck(String id, String userName, String magazineName, String month, String copiesNum, String totalPrice) {
        String errorMessage = null;

        // 检查ID
        // chcek ID
        if (id.length() == 0) {
            return errorMessage = "id cannot be empty";
        } else if(id.length() > 11) {
            return  errorMessage = "id too long";
        } else {
            try {
                Integer.parseInt(id);
            } catch (Exception e) {
                return errorMessage = "id contains only numbers";
            }

            JdbcUtils jdbcUtils = new JdbcUtils();
            jdbcUtils.getConnection();
            String sql = "select * from `order` where id = ?";
            List<Object> params = new ArrayList<>();
            params.add(Integer.parseInt(id));

            try {
                Map<String, Object> same = jdbcUtils.findSimpleResult(sql, params);
                if (!same.isEmpty()) {
                    return errorMessage = "id already registered";
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //检查用户姓名
        //check user's name
        if (userName == null || userName.length() == 0) {
            return errorMessage = "empty name";
        } else {
            JdbcUtils jdbcUtils = new JdbcUtils();
            jdbcUtils.getConnection();
            System.out.println(userName);
            String sql = "select * from `commonUser` where username = ?";
            List<Object> params = new ArrayList<>();
            params.add(userName);
            try {
                Map<String, Object> same = jdbcUtils.findSimpleResult(sql, params);
                if (same.isEmpty()) {
                    return errorMessage = "username not exist";
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //检查杂志名
        //check magazine name
        if (magazineName == null || magazineName.length() == 0) {
            return errorMessage = "empty magazine name";
        } else {
            JdbcUtils jdbcUtils = new JdbcUtils();
            jdbcUtils.getConnection();

            System.out.println(magazineName);
            String sql = "select * from `magazine` where `name` = ?";
            List<Object> params = new ArrayList<>();
            params.add(magazineName);
            System.out.println(params);
            try {
                Map<String, Object> same = jdbcUtils.findSimpleResult(sql, params);
                if (same.isEmpty()) {
                    return errorMessage = "magazine name not exist";
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //检查月份
        //check month

        //检查份数
        //check copies

        //检查总金额
        //check amount
        if (totalPrice == null || totalPrice.length() == 0) {
            errorMessage = "empty order amount";
        } else if(totalPrice.length() > 11) {
            return  errorMessage = "id too long";
        } else {
            try {
                Integer.parseInt(totalPrice);
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "numbers only";
            }
        }

        return errorMessage;

    }

    public static String magazineClassSignUpCheck(String id, String className) {
        String errorMessage = null;

        //检查ID;
        //check ID
        if (id == null || id.length() == 0) {
            return errorMessage = "empty id";
        } else if(id.length() > 11) {
            return  errorMessage = "id too long";
        } else {
            try {
                Integer.parseInt(id);
            } catch (Exception e) {
                return  errorMessage = "id should contain numbers";
            }
            JdbcUtils jdbcUtils = new JdbcUtils();
            jdbcUtils.getConnection();

            String sql = "select * from `mClass` where id = ?";
            List<Object> params = new ArrayList<>();
            params.add(Integer.parseInt(id));
            try {
                Map<String, Object> same = jdbcUtils.findSimpleResult(sql, params);
                if (!same.isEmpty()) {
                    return errorMessage = "id already registered";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //检查分类名
        //check category
        if (className == null || className.length() == 0) {
            errorMessage = "empty category name";
        } else {
            JdbcUtils jdbcUtils = new JdbcUtils();
            jdbcUtils.getConnection();
            String sql = "select * from `mClass` where className = ?";
            List<Object> params = new ArrayList<>();
            params.add(className);
            try {
                Map<String, Object> same = jdbcUtils.findSimpleResult(sql, params);
                if (!same.isEmpty()) {
                    errorMessage = "category name already used";
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return errorMessage;
    }

    public static String orderUpdateCheck(Order old, String id, String userName, String magazineName, String month, String copiesNum, String totalPrice) {
        String errorMessage = null;

        // 检查id
        // check id
        if (id == null || id.length() == 0){
            return errorMessage = "empty id";
        } else if(id.length() > 11) {
            return  errorMessage = "id too long";
        } else {
            try {
                Integer.parseInt(id);
            } catch (Exception e) {
                e.printStackTrace();
                return errorMessage = "id contains only numbers";
            }
            if (old.getId() != Integer.parseInt(id)) {
                return errorMessage = "cannot alter category's id; you can delete category then create new";
            }
        }

        // 检查用户名
        // check username
        if (userName == null || userName.length() == 0) {
            return errorMessage = "empty username";
        } else {
            JdbcUtils jdbcUtils = new JdbcUtils();
            jdbcUtils.getConnection();

            String sql = "select * from `commonUser` where username = ?";
            List<Object> params = new ArrayList<>();
            params.add(userName);
            try {
                Map<String, Object> same = jdbcUtils.findSimpleResult(sql, params);
                if (same.isEmpty()) {
                    return  errorMessage = "user not exist";
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //检查杂志名
        //check magazine name
        if (magazineName == null || magazineName.length() == 0) {
            return errorMessage = "empty magazine name";
        } else {
            JdbcUtils jdbcUtils = new JdbcUtils();
            jdbcUtils.getConnection();

            String sql = "select * from `magazine` where name = ?";
            List<Object> params = new ArrayList<>();
            params.add(magazineName);
            try {
                Map<String, Object> same = jdbcUtils.findSimpleResult(sql, params);
                if (same.isEmpty()) {
                    return errorMessage = "magazine not exist";
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // 检查订单总金额
        //check order amount
        if (totalPrice == null || totalPrice.length() == 0) {
            errorMessage = "empty order amount";
        } else if(totalPrice.length() > 11) {
            return  errorMessage = "order amount exceed limit";
        } else {
            try {
                Integer.parseInt(totalPrice);
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "order amount is numbers only";
            }
        }
        return errorMessage;
    }

    public static String magazineClassUpdateCheck(MagazineClass old, String id, String className) {
        String errorMessage = null;


        // 检查id
        // check id
        if (id == null || id.length() == 0) {
            return errorMessage = "empty id";
        } else if(id.length() > 11) {
            return  errorMessage = "id too long";
        } else {
            try {
                Integer.parseInt(id);
            } catch (Exception e) {
                return errorMessage = "id contains only numbers";
            }

            if (old.getId() != Integer.parseInt(id)) {
                return errorMessage = "cannot alter category's id; you can delete category then create new";
            }
        }


        // 检查分类名
        // check category name
        if (className == null || className.length() == 0) {
            errorMessage = "empty category name";
        } else {
            JdbcUtils jdbcUtils = new JdbcUtils();
            jdbcUtils.getConnection();
            String sql = "select * from `mClass` where id not in (?) and className = ?";
            List<Object> params = new ArrayList<>();
            params.add(old.getName());
            params.add(className);
            try {
                Map<String, Object> same = jdbcUtils.findSimpleResult(sql, params);
                if (!same.isEmpty()) {
                    errorMessage = "category name already used";
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //检查是否时默认分类
        //check if in default category
        if (old.getName().equals("未分类")) {
            errorMessage = "default category cannot be altered";
        }
        return errorMessage;
    }
}
