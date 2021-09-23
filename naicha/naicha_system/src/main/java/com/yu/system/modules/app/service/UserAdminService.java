package com.yu.system.modules.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.common.entity.app.User;
import com.yu.system.modules.app.mapper.UserAdminMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class UserAdminService {

    @Resource
    private UserAdminMapper userAdminMapper;

    /**
     * 查询所有
     */
    public List<User> getAllUserAdmins() {
        return userAdminMapper.selectList(null);
    }

    /**
     * 分页条件查询
     *
     * @param pageNo   页号
     * @param pageSize 页面大小
     * @return Page<UserAdmin>
     */
    public Page<User> getUserAdminByPage(int pageNo, int pageSize) {
        Page<User> page = new Page<>(pageNo, pageSize);
        return userAdminMapper.selectPage(page, null);
    }

    // 增加
    @Transactional
    public int addUserAdmin(User user) {
        return userAdminMapper.insert(user);
    }

    // 批量删除
    @Transactional
    public int deleteUserAdminBatchIds(List<Integer> userAdminIdList) {
        return userAdminMapper.deleteBatchIds(userAdminIdList);
    }

    // 修改
    @Transactional
    public int updateUserAdmin(User user) {
        return userAdminMapper.updateById(user);
    }
}
