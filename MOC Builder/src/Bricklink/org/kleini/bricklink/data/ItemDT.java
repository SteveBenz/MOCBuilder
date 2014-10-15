/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.data;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * {@link ItemDT}
 *
 * @author <a href="mailto:hismelf@kleini.org">Marcus Klein</a>
 */
public class ItemDT {

    private String itemNo, itemName, alternateNo, imageUrl, thumbnailUrl, dimX, dimY, dimZ, description, languageCode;
    private ItemType itemType;
    private int yearReleased;
    private float weight;
    private boolean isObsolete;
    /**
     * The main category of the item
     */
    private CategoryT category;
    

    public ItemDT() {
        super();
    }

    @JsonProperty("no")
    public String getItemNo() {
        return itemNo;
    }

    @JsonProperty("no")
    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }


    @JsonProperty("name")
    public String getName() {
        return this.itemName;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.itemName = name;
    }
    
    @JsonProperty("type")
    public String getType() {
        return this.itemType.toString();
    }

    @JsonProperty("type")
    public void setType(String name) {
        this.itemType = ItemType.valueOf(name);
    }
    

    @JsonProperty("categoryID")
    public void setCategoryID(int categoryID)  {
        try {
			this.category = CategoryT.byId(categoryID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.category = CategoryT.OTHER;
		}
    }
    
    @JsonProperty("alternate_no")
    public String getAlternateNo() {
        return this.alternateNo;
    }

    @JsonProperty("alternate_no")
    public void setAlternateNo(String alternateNo) {
        this.alternateNo = alternateNo;
    }
    
    @JsonProperty("image_url")
    public String getImageUrl() {
        return this.imageUrl;
    }

    @JsonProperty("image_url")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    @JsonProperty("thumbnail_url")
    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    @JsonProperty("thumbnail_url")
    public void setThumbnailUrl(String url) {
        this.thumbnailUrl = url;
    }
    
    @JsonProperty("weight")
    public float getWeight() {
        return this.weight;
    }

    @JsonProperty("weight")
    public void setWeight(float weight) {
        this.weight = weight;
    }
    
    @JsonProperty("dim_x")
    public String getDimX() {
        return this.dimX;
    }

    @JsonProperty("dim_x")
    public void setDimX(String value) {
        this.dimX = value;
    }
    
    @JsonProperty("dim_y")
    public String getDimY() {
        return this.dimY;
    }

    @JsonProperty("dim_y")
    public void setDimY(String value) {
        this.dimY = value;
    }
    
    @JsonProperty("dim_z")
    public String getDimZ() {
        return this.dimZ;
    }

    @JsonProperty("dim_z")
    public void setDimZ(String value) {
        this.dimZ = value;
    }
    
    @JsonProperty("year_released")
    public int getYearReleased() {
        return this.yearReleased;
    }

    @JsonProperty("year_released")
    public void setYearReleased(int value) {
        this.yearReleased = value;
    }
    
    @JsonProperty("description")
    public String getDescription() {
        return this.description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }
    
    @JsonProperty("is_obsolete")
    public boolean getIsObsolete() {
        return this.isObsolete;
    }

    @JsonProperty("is_obsolete")
    public void setIsObsolete(boolean isObsolete) {
        this.isObsolete = isObsolete;
    }
    
    @JsonProperty("language_code")
    public String getLanguageCode() {
        return this.languageCode;
    }

    @JsonProperty("language_code")
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
    
    @Override
    public String toString(){
    	return itemNo+", "+itemName;
    }
    
    public CategoryT getCategory() {
        return category;
    }

    public void setCategory(CategoryT category) {
        this.category = category;
    }
}
