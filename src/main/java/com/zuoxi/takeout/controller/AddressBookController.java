package com.zuoxi.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zuoxi.takeout.common.BaseContext;
import com.zuoxi.takeout.common.R;
import com.zuoxi.takeout.entity.AddressBook;
import com.zuoxi.takeout.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;


    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentUid());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }


    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentUid());
        updateWrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(updateWrapper);

        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }


    /**
     * 查询地址列表
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list() {
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentUid());
        wrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(wrapper);

        return R.success(list);
    }


    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        // SQL: SELECT * FROM address_book WHERE user_id = ? AND is_default = 1;
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentUid());
        wrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook defaultAddress = addressBookService.getOne(wrapper);
        if (defaultAddress == null) {
            return R.error("没有查到默认地址信息");
        }
        return R.success(defaultAddress);
    }


    /**
     * 根据id查询地址信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id) {
        // SQL: SELECT * FROM address_book WHERE id = ?;
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook == null) {
            return R.error("没有对应地址信息");
        }
        return R.success(addressBook);
    }


    /**
     * 更新地址信息
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        // SQL: UPDATE address_book SET key=value WHERE id = ?;
        addressBookService.updateById(addressBook);

        return R.success("地址更新成功");
    }


    /**
     * 删除地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> remove(Long ids) {
        addressBookService.removeById(ids);
        return R.success("删除地址成功");
    }
}
























