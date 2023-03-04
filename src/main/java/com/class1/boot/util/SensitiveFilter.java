package com.class1.boot.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private final String REPLACE_WORD = "***";
    //根节点
    private TireNode rootNode = new TireNode();

    //初始化敏感词前缀树
    @PostConstruct
    private void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))
        ){
            String keyword;
            while ((keyword = bufferedReader.readLine())!=null){
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("读取敏感词文件失败！"+e.getMessage());
        }
    }
    //将敏感词添加到前缀树中
    private void addKeyword(String keyword) {
        TireNode timeNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            if(timeNode.getSonNodes(c)==null){
                timeNode.addSonTireNode(c,new TireNode());
            }
            timeNode=timeNode.getSonNodes(c);
            if(i==keyword.length()-1){
                timeNode.setKeywordEnd(true);
            }
        }
    }

    //前缀树
    private class TireNode{
        // 关键词结束标志
        private Boolean isKeywordEnd = false;
        //子节点
        private Map<Character,TireNode> sonNodes = new HashMap<>();


        public Boolean getKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(Boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSonTireNode(Character c,TireNode node){
            sonNodes.put(c,node);
        }

        public TireNode getSonNodes(Character c){
            return sonNodes.get(c);
        }
    }
    //过滤敏感词
    public  String filterSensitiveKeyword(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        int begin = 0;
        int position = 0;
        TireNode tempNode = rootNode;
        StringBuilder sb = new StringBuilder();
        while (position<text.length()){
            Character c = text.charAt(position);
            if(isSymbol(c)){
                if(tempNode==rootNode){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }
           //检查下一级节点
            tempNode = tempNode.getSonNodes(c);
            if(tempNode==null){
                sb.append(c);
                position=++begin;
                tempNode=rootNode;
            } else if (tempNode.getKeywordEnd()) {
                sb.append(REPLACE_WORD);
                begin = ++position;
                tempNode=rootNode;
            }else {
                position++;
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80||c > 0x9FFF);
    }
}
