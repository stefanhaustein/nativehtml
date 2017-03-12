public class AndroidDocunent extends Document {

    private final Context context;

    public AndroidDocument(Context context) {
        this.context = context;
    }

    protected Element createElemenent(ElementType type, String name) {
        switch (ElementType type, String name) {
            COMPONENT_CONTAINER:
                return new HtmlContainer(context, name);

            ELEMNET_DATA:
                return new ElementDataElement(name);

            TEXT_DATA:
                return new TextDataElement(name);

            LEAF_COMPONENT:
                return new LeafComponent(context, name);

            TEXT_COMPONENT:
                return new TextComponent(context, name);

            default:
                throw new RuntimeException("ElementType: " + type);
        }
    }
}