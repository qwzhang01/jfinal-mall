package cn.qw.render;

import com.jfinal.log.Log;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.OutputStream;

public class ExcelRender extends Render {
    private final static String CONTENT_TYPE = "application/msexcel;charset="
            + getEncoding();
    protected final Log LOG = Log.getLog(getClass());
    private String fileName = "file1.xls";
    private Workbook book;

    @Override
    public void render() {
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename="
                + fileName);
        response.setContentType(CONTENT_TYPE);
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            book.write(os);
        } catch (Exception e) {
            throw new RenderException(e);
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }

        }
    }

    public ExcelRender fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ExcelRender workbook(Workbook book) {
        this.book = book;
        return this;
    }
}
