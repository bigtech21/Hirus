package data;

import java.util.Vector;

public class Words {
    public Vector<String> wordsAns = new Vector<String>();
    public Vector<String> wordsAsk = new Vector<String>();
    public Words(){
        wordsAsk.add("국내에서 감염병이 의심될 때 \n질병관리청 콜센터를 통해 도움을 \n받을 수 있다.\n" +
                "이 때 질병관리청 콜센터의 번호는?");
        wordsAns.add("1339");

        wordsAsk.add("<보기>\n[버스][지하철][배][비행기]\n 상단의 보기 중 전염병이 유행하는 중에 그나마 가장 안전한 대중 교통 수단은 \n무엇인가?\n");
        wordsAns.add("비행기");

        wordsAsk.add("<보기>\n[장티푸스 / 크로이츠펠트야곱병 / \n폴리오 / 보틀리늄 독소증]\n다음 중 일시적으로 식품업종(ex : 단체 급식소 등) 종사의 제한을 \n받는 감염병은?\n");
        wordsAns.add("장티푸스");

        wordsAsk.add("공기 감염과 비말 감염의 차이점은?\n [힌트]\n ○○○의 접촉 여부");
        wordsAns.add("분비물");

        wordsAsk.add("제 1급 감염병 일부와 제 2급 감염병 일부의 경우 ○○○ ○○○○○○ ○○○○의 대상이 된다. \n이것은 무엇인가?");
        wordsAns.add("어린이 국가예방접종 지원사업");
    }
}
