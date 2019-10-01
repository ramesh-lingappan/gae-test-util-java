package com.github.rameshl.appengine.testing.helper;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;

import lombok.Data;

/**
 * appengine-testing-utils Created by ramesh on 2019-10-01.
 */

@Data
@Entity
public class TestEntity implements Serializable {

    private static final long serialVersionUID = -3313747449819392920L;

    @Id
    private Long id;
}
