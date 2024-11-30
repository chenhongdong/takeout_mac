package com.zuoxi.takeout.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("setmeal")
@ApiModel("套餐")
public class Setmeal implements Serializable {
    private static final long serialVersionUid = 1L;

    @ApiModelProperty("主键")
    private Long id;
    @ApiModelProperty("分类id")
    private Long categoryId;    // 分类id
    @ApiModelProperty("套餐名称")
    private String name;
    @ApiModelProperty("套餐价格")
    private BigDecimal price;
    @ApiModelProperty("状态")
    private Integer status;
    @ApiModelProperty("套餐编号")
    private String code;
    @ApiModelProperty("描述信息")
    private String description;
    @ApiModelProperty("图片")
    private String image;
    @ApiModelProperty("是否删除")
    private Integer isDeleted;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人")
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @ApiModelProperty("更新人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
