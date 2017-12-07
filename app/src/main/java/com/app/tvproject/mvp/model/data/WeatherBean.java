package com.app.tvproject.mvp.model.data;

import java.util.List;

/**
 * Created by www on 2017/12/7.
 */

public class WeatherBean {

    public ResultBean result;
    public String msg;
    public int code;

    public static class ResultBean {
        public List<HeWeather5Bean> HeWeather5;

        public static class HeWeather5Bean {
            public NowBean now;
            public SuggestionBean suggestion;
            public AqiBean aqi;
            public BasicBean basic;
            public String status;
            public List<DailyForecastBean> daily_forecast;

            public static class NowBean {
                public String hum;
                public String vis;
                public String pres;
                public String pcpn;
                public String fl;
                public String tmp;
                public CondBean cond;
                public WindBean wind;

                public static class CondBean {
                    public String txt;
                    public String code;
                }

                public static class WindBean {
                    public String sc;
                    public String spd;
                    public String deg;
                    public String dir;
                }
            }

            public static class SuggestionBean {
                public UvBean uv;
                public CwBean cw;
                public TravBean trav;
                public AirBean air;
                public ComfBean comf;
                public DrsgBean drsg;
                public SportBean sport;
                public FluBean flu;

                public static class UvBean {
                    public String txt;
                    public String brf;
                }

                public static class CwBean {
                    public String txt;
                    public String brf;
                }

                public static class TravBean {
                    public String txt;
                    public String brf;
                }

                public static class AirBean {
                    public String txt;
                    public String brf;
                }

                public static class ComfBean {
                    public String txt;
                    public String brf;
                }

                public static class DrsgBean {
                    public String txt;
                    public String brf;
                }

                public static class SportBean {
                    public String txt;
                    public String brf;
                }

                public static class FluBean {
                    public String txt;
                    public String brf;
                }
            }

            public static class AqiBean {
                public CityBean city;

                public static class CityBean {
                    public String no2;
                    public String o3;
                    public String pm25;
                    public String qlty;
                    public String so2;
                    public String aqi;
                    public String pm10;
                    public String co;
                }
            }

            public static class BasicBean {
                public String city;
                public UpdateBean update;
                public String lon;
                public String id;
                public String cnty;
                public String lat;

                public static class UpdateBean {
                    public String loc;
                    public String utc;
                }
            }

            public static class DailyForecastBean {
                public String date;
                public String pop;
                public String hum;
                public String uv;
                public String vis;
                public AstroBean astro;
                public String pres;
                public String pcpn;
                public TmpBean tmp;
                public CondBeanX cond;
                public WindBeanX wind;

                public static class AstroBean {
                    public String ss;
                    public String mr;
                    public String ms;
                    public String sr;
                }

                public static class TmpBean {
                    public String min;
                    public String max;
                }

                public static class CondBeanX {
                    public String txt_n;
                    public String code_n;
                    public String code_d;
                    public String txt_d;
                }

                public static class WindBeanX {
                    public String sc;
                    public String spd;
                    public String deg;
                    public String dir;
                }
            }
        }
    }
}
