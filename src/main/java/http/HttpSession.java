package http;

import java.util.HashMap;
import java.util.Map;

public class HttpSession { //클라이언트 별 세션 데이터 관리
    private Map<String, Object> values = new HashMap<>(); //상태 데이터 저장
    private String id; //세션 id 저장

    public HttpSession(String id){ //생성자 : id 값 부여
        this.id = id;
    }

    //다섯가지 핵심 메서드 구현
    public String getId(){
        return id;
    }
    public void setAttribute(String name, Object value){
        values.put(name, value);
    }
    public Object getAttribute(String name){
        return values.get(name);
    }
    public void removeAttribute(String name){
        values.remove(name);
    }
    public void invalidate(){
        values.clear();
    }

}
