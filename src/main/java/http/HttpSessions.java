package http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {
    //static 을 사용한 이유
    //서버가 다수의 클라이언트를 관리 -> 즉 저장소를 매번 생성한는 것이 아니라 한 번 생성 후 재사용

    private static Map<String, HttpSession> sessions = new HashMap<>();

    public static HttpSession getSession(String id){
        HttpSession session = sessions.get(id);

        if(session==null){
            session = new HttpSession(id);
            sessions.put(id, session);
        }

        return session;
    }

    public static void removeSession(String id){
        sessions.remove(id);
    }

}
