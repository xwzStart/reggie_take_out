package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie.entity.AddressBook;
import com.it.reggie.mapper.AddressBookMapper;
import com.it.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author xwzStart
 * @create 2022-03-09 19:35
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
