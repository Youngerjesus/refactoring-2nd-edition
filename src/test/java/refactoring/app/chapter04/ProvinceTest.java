package refactoring.app.chapter04;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ProvinceTest {

    Province sampleProvince;

    @BeforeEach
    void setUp() {
        sampleProvince = SampleProvinceFactory.getSampleProvince();
    }

    @Test
    void shortfallTest(){
        //given
        int answer = 5;
        //when
        int result = sampleProvince.shortFall();
        //then
        assertEquals(answer, result);
    }

    @Test
    void profitTest(){
        //given
        int answer = 230;
        //when
        int result = sampleProvince.profit();
        //then
        assertEquals(answer, result);
    }
    
    @Test
    void changeProductionTest(){
        //given
        int shortFall = -6;
        int profit = 292;
        //when
        sampleProvince.producers.get(0).setProduction(20);
        int actualShortFall = sampleProvince.shortFall();
        int actualProfit = sampleProvince.profit();
        //then
        assertEquals(shortFall, actualShortFall);
        assertEquals(profit, actualProfit)  ;
    }

    @Test
    void noProducersTest(){
        //given
        Province province = new Province("No Producers", new ArrayList<>(), 30, 20);
        int shortFall = 30;
        int profit = 0;
        //when
        int actualShortFall = province.shortFall();
        int actualProfit = province.profit();
        //then
        assertEquals(shortFall, actualShortFall);
        assertEquals(profit, actualProfit);
    }
}