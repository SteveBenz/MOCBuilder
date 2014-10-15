/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.api.Inventory;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;
import Bricklink.org.kleini.bricklink.data.CategoryDT;
import Bricklink.org.kleini.bricklink.data.ColorDT;
import Bricklink.org.kleini.bricklink.data.ItemType;

/**
 * {@link InventoriesRequest}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class InventoriesRequest implements Request<InventoriesResponse> {

    private final ItemType type;

    private final Status status;

    private final CategoryDT category;

    private final ColorDT color;

    public InventoriesRequest(ItemType type, Status status, CategoryDT category, ColorDT color) {
        super();
        this.type = type;
        this.status = status;
        this.category = category;
        this.color = color;
    }

    @Override
    public String getPath() {
        return "inventories";
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[] {
            // API description is wrong. The value must not be "part". It must be "P".
            new Parameter("item_type", type.getLongId()),
            new Parameter("status", status.getIdentifier()),
            new Parameter("category_id", category.getIdentifier()),
            new Parameter("color_id", color.getIdentifier())
        };
    }

    @Override
    public InventoriesParser getParser() {
        return new InventoriesParser();
    }

    public enum Status {
        AVAILABLE('Y'),
        STOCKROOM_A('S');

        private final char identifier;

        Status(char identifier) {
            this.identifier = identifier;
        }

        public char getIdentifier() {
            return identifier;
        }
    }
    
    @Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
