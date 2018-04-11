package com.fatwong.encore.bean;

import java.util.List;

/**
 */
public class Dynamic {


    private BodyBean body;
    private HeaderBean header;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

    public static class BodyBean {

        private PagerBean pager;
        private String sid;


        private List<DetailBean> detail;

        public PagerBean getPager() {
            return pager;
        }

        public void setPager(PagerBean pager) {
            this.pager = pager;
        }

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        public List<DetailBean> getDetail() {
            return detail;
        }

        public void setDetail(List<DetailBean> detail) {
            this.detail = detail;
        }

        public static class PagerBean {
            private String pagesize;
            private String totalRows;
            private int pageNext;
            private String currentPage;

            public String getPagesize() {
                return pagesize;
            }

            public void setPagesize(String pagesize) {
                this.pagesize = pagesize;
            }

            public String getTotalRows() {
                return totalRows;
            }

            public void setTotalRows(String totalRows) {
                this.totalRows = totalRows;
            }

            public int getPageNext() {
                return pageNext;
            }

            public void setPageNext(int pageNext) {
                this.pageNext = pageNext;
            }

            public String getCurrentPage() {
                return currentPage;
            }

            public void setCurrentPage(String currentPage) {
                this.currentPage = currentPage;
            }
        }

        public static class DetailBean {
            private String opt5;
            private String opt3;
            private String isopt5;
            private String opt4;
            private String nickname;
            private String isopt1;
            private String userid;
            private String photo;
            private String url;
            private String createtime;
            private String id;
            private String title;
            private String shareurl;
            private String source;
            private String mp4Url;
            private String opt2;
            private String opt1;

            public String getOpt5() {
                return opt5;
            }

            public void setOpt5(String opt5) {
                this.opt5 = opt5;
            }

            public String getOpt3() {
                return opt3;
            }

            public void setOpt3(String opt3) {
                this.opt3 = opt3;
            }

            public String getIsopt5() {
                return isopt5;
            }

            public void setIsopt5(String isopt5) {
                this.isopt5 = isopt5;
            }

            public String getOpt4() {
                return opt4;
            }

            public void setOpt4(String opt4) {
                this.opt4 = opt4;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getIsopt1() {
                return isopt1;
            }

            public void setIsopt1(String isopt1) {
                this.isopt1 = isopt1;
            }

            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public String getPhoto() {
                return photo;
            }

            public void setPhoto(String photo) {
                this.photo = photo;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getShareurl() {
                return shareurl;
            }

            public void setShareurl(String shareurl) {
                this.shareurl = shareurl;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getMp4Url() {
                return mp4Url;
            }

            public void setMp4Url(String mp4Url) {
                this.mp4Url = mp4Url;
            }

            public String getOpt2() {
                return opt2;
            }

            public void setOpt2(String opt2) {
                this.opt2 = opt2;
            }

            public String getOpt1() {
                return opt1;
            }

            public void setOpt1(String opt1) {
                this.opt1 = opt1;
            }
        }
    }

    public static class HeaderBean {
        private String result;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
}

