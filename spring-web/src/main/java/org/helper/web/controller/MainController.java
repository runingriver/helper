package org.helper.web.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.helper.common.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    /**
     * 查看线程栈
     * http://localhost:8080/stack
     */
    @RequestMapping(value = "stack")
    public String getAllThreadStack() {
        return "stack";
    }


    @ResponseBody
    @RequestMapping(value = "/json1")
    public List<String> returnJsonData1() {
        List<String> result = Lists.newArrayList();
        result.add("test string 1");
        result.add("test string 2");
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/json2")
    public String returnJsonData2() {
        List<HashMap<String, String>> result = Lists.newArrayList();
        HashMap<String, String> map = Maps.newHashMap();
        map.put("item1", "hello world1.");
        map.put("item2", "hello world2.");
        map.put("item3", "hello world3.");
        result.add(map);
        HashMap<String, String> map2 = Maps.newHashMap();
        map2.put("status", "success.");
        result.add(map2);
        return JSON.toJSONString(result);
    }

    @ResponseBody
    @RequestMapping(value = "/json3")
    public ResponseEntity<List<String>> returnJsonData3() {
        List<String> result = Lists.newArrayList();
        result.add("test string 1");
        result.add("test string 2");
        return new ResponseEntity<List<String>>(result, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/json33")
    public ResponseEntity<HashMap<String, String>> returnJsonData33() {
        HashMap<String, String> map = Maps.newHashMap();
        map.put("item1", "hello world1.");
        map.put("item2", "hello world2.");
        map.put("item3", "hello world3.");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @ResponseBody
    @RequestMapping(value = "/json4")
    public JsonView<Map<String, String>> returnJsonData4() {
        HashMap<String, String> map = Maps.newHashMap();
        map.put("item1", "hello world1.");
        map.put("item2", "hello world2.");
        map.put("item3", "hello world3.");
        JsonView<Map<String, String>> mapJsonView = new JsonView<Map<String, String>>(true, map);
        return mapJsonView;
    }

    // @ResponseBody会将一个对象转换成json,但是对于null的成员变量也会加入json串中.
    @ResponseBody
    @RequestMapping(value = "/json44")
    public String returnJsonData44() {
        Person person = new Person(22, "zongzhe.hu");
        JsonView<Person> mapJsonView = new JsonView<>(true, person);
        return JSON.toJSONString(mapJsonView);
    }

    @ResponseBody
    @RequestMapping(value = "/json444")
    public ResponseEntity<Person> returnJsonData444() {
        Person person = new Person(22, "zongzhe.hu");
        return new ResponseEntity<Person>(person,HttpStatus.OK);
    }

    public static class Person {
        private String name;
        private int age;

        public Person(int age, String name) {
            this.age = age;
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "age=" + age +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
