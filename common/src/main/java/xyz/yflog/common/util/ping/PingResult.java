package xyz.yflog.common.util.ping;

/**
 * Created by vincent on 15/6/7.
 */
public class PingResult {
    private long[] _results;
    private boolean _hasError;
    private String _error;

    public PingResult(String _error) {
        this._hasError = true;
        this._error = _error;

        this._results = new long[5];
        for (int i = 0; i < 5; i++) {
            _results[i] = 0L;
        }
    }

    public PingResult(long[] _results) {
        this._results = _results;
        this._hasError = false;
        this._error = "";
    }

    public boolean hasError() {
        return _hasError;
    }

    public String getError() {
        return _error;
    }

    public long get(int idx) {
        return _results[idx];
    }

    public long[] getAll() {
        return _results;
    }

}
