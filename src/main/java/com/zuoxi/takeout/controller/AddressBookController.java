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
    public R<String> save(@RequestBody AddressBook addressBook) {
        Long currentUid = BaseContext.getCurrentUid();
        addressBook.setUserId(currentUid);
        addressBookService.save(addressBook);
        return R.success("添加地址成功");
    }


    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook) {
        Long currentUid = BaseContext.getCurrentUid();
        // 把所有地址都设置为非默认
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, currentUid);
        wrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(wrapper);

        // 设置当前地址为默认
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("设置默认地址成功");
    }

    /**
     * 查询地址列表
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list() {
        Long currentUid = BaseContext.getCurrentUid();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, currentUid);
        wrapper.orderByDesc(AddressBook::getCreateTime);
        List<AddressBook> list = addressBookService.list(wrapper);
        return R.success(list);
    }


    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        Long currentUid = BaseContext.getCurrentUid();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, currentUid);
        wrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(wrapper);
        if (addressBook == null) {
            return R.error("目前还没有默认地址");
        }
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook == null) {
            return R.error("该地址信息不存在");
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
        addressBookService.updateById(addressBook);
        return R.success("更新地址成功");
    }

    /**
     * 删除地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        addressBookService.removeById(ids);
        return R.success("删除地址成功");
    }
}
























