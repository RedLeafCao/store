/**
 * @Author kjlin
 * @Date 11/14/18 5:39 PM
 */
package com.can.store.shopping.commons.kizz.lib.utils;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class Paginate {
    private int[] _paginate = {
            0,//count,0
            20,//rows,1
            0,//pages,2
            1,//current,3
            1,//prev,4
            1,//begin,5
            1,//end,6
            1,//next,7
    };

    public Paginate() {

    }

    public Paginate(int count, int rows, int page) {
        if (0 < count) {
            this.init(count, rows, page);
        }
    }

    protected void _calc() {
        int prev = this.getCurrent() - 1;
        prev = (1 > prev) ? 1 : prev;

        int next = this.getCurrent() + 1;
        next = (next > this.getPages()) ? this.getPages() : next;

        this._paginate[4] = prev;
        this._paginate[7] = next;

        if (6 > this.getPages()) {
            //不超过5页的，
            this._paginate[5] = 1;
            this._paginate[6] = this.getPages();
            return;
        }

        if (2 > this._paginate[3]) {
            // 左边不足2的，另外一边补足
            this._paginate[5] = 1;
            this._paginate[6] = 5;
            return;
        }

        if (2 > (this.getPages() - this.getCurrent())) {
            // 右边不足2的，另外一边补足
            this._paginate[5] = this.getPages() - 5;
            this._paginate[6] = this.getPages();
            return;
        }

        this._paginate[5] = this.getCurrent() - 2;
        this._paginate[6] = this.getCurrent() + 2;
    }

    public boolean init(int count, int rows, int page) {
        if (1 > count) {
            return false;
        }

        this._paginate[0] = count;
        this._paginate[1] = rows;
        this._paginate[2] = (int) (Math.ceil(count / (rows * 1.0)));
        this._paginate[3] = 1;
        if (1 == page) {
            this._calc();
        } else {
            this.jumpto(page);
        }
        return true;
    }

    public int getCount() {
        return this._paginate[0];
    }

    public int getRows() {
        return this._paginate[1];
    }

    public int getPages() {
        return this._paginate[2];
    }

    public int getCurrent() {
        return this._paginate[3];
    }

    public int getPrev() {
        return this._paginate[4];
    }

    public int getBegin() {
        return this._paginate[5];
    }

    public int getEnd() {
        return this._paginate[6];
    }

    public int getNext() {
        return this._paginate[7];
    }

    public boolean jumpto(int page) {
        if (this.getCurrent() == page) {
            return true;
        }
        if (1 > page || this.getPages() < page) {
            return false;
        }

        this._paginate[3] = page;
        this._calc();
        return true;
    }

    public boolean pageDown(int n) {
        int page = this.getCurrent() - n;
        if (0 > page) {
            return false;
        }

        this._paginate[3] = page;
        this._calc();
        return true;
    }

    public boolean pageUp(int n) {
        int page = this.getCurrent() + n;
        if (this.getPages() < page) {
            return false;
        }

        this._paginate[3] = page;
        this._calc();
        return true;
    }
}
