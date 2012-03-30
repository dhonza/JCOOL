package cz.cvut.felk.cig.jcool.ui;

import java.awt.Dimension;
import java.util.EventObject;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * A list with the selected item bigger than the others
 * @author Christophe Le besnerais
 * @author ytoh (few modifications)
 * @link http://swing-fx.blogspot.com/
 */
public class JExpandList extends JTable {

    /**
     * Constructs a JExpandList that displays the elements in the
     * specified, non-null model. All JList
     * constructors delegate to this one.
     *
     * @param dataModel the data model for this list
     * @exception IllegalArgumentException if dataModel is null
     */
    public JExpandList(ListModel dataModel) {
        super(new ProxyTableModel(dataModel));
        this.setDefaultEditor(Object.class, null);

        // remove unused stuffs from JTable :
        this.setTableHeader(null);
        this.setShowGrid(false);
        this.setIntercellSpacing(new Dimension(0, 0));
        this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * ListModel adapter
     */
    static class ProxyTableModel extends AbstractTableModel implements TableModel {
        private ListModel dataModel;

        public ProxyTableModel(ListModel dataModel) {
            setListModel(dataModel);
        }

        void setListModel(ListModel dataModel) {
            this.dataModel = dataModel;
        }

        public int getRowCount() {
            return dataModel.getSize();
        }

        public int getColumnCount() {
            return 1;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return dataModel.getElementAt(rowIndex);
        }
    }


    /**
     * Constructs a JExpandList that displays the elements in the
     * specified array. This constructor just delegates to the
     * ListModel constructor.
     *
     * @param listData the array of Objects to be loaded into the data model
     */
    public JExpandList(final Object[] listData) {
        this(new AbstractListModel() {

            public int getSize() {
                return listData.length;
            }

            public Object getElementAt(int i) {
                return listData[i];
            }
        });
    }

    /**
     * Constructs a JExpandList that displays the elements in the
     * specified Vector. This constructor just delegates to the
     * ListModel constructor.
     *
     * @param listData the Vector to be loaded into the data model
     */
    public JExpandList(final Vector listData) {
        this(new AbstractListModel() {

            public int getSize() {
                return listData.size();
            }

            public Object getElementAt(int i) {
                return listData.elementAt(i);
            }
        });
    }

    /**
     * Constructs a JExpandList with an empty model.
     */
    public JExpandList() {
        this(new AbstractListModel() {

            public int getSize() {
                return 0;
            }

            public Object getElementAt(int i) {
                return "No Data Model";
            }
        });
    }

    public void setListModel(ListModel model) {
        ((ProxyTableModel)this.getModel()).setListModel(model);
    }

    public void setDefaultRenderer(TableCellRenderer renderer) {
        super.setDefaultRenderer(Object.class, renderer);
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        // "one click" edit :
        selectionModel.setSelectionInterval(row, row);
        return super.editCellAt(row, column, e);
    }
}