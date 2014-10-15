/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;

import Bricklink.org.kleini.bricklink.api.BrickLinkClient;
import Bricklink.org.kleini.bricklink.api.Configuration;
import Bricklink.org.kleini.bricklink.api.ConfigurationProperty;
import Bricklink.org.kleini.bricklink.api.order.OrderRequest;
import Bricklink.org.kleini.bricklink.api.order.OrdersRequest;
import Bricklink.org.kleini.bricklink.api.order.OrdersResponse;
import Bricklink.org.kleini.bricklink.data.AddressDT;
import Bricklink.org.kleini.bricklink.data.NameDT;
import Bricklink.org.kleini.bricklink.data.OrderDT;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * This class is a starter and should read the customer address information for not yet booked orders.
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class CSVExport {

    public CSVExport() {
        super();
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        OrderHelper helper = new OrderHelper(configuration.getProperty(ConfigurationProperty.COMMENT_REGEX));
        BrickLinkClient client = new BrickLinkClient(configuration);
        List<String[]> output = new ArrayList<String[]>();
        try {
            OrdersRequest request = new OrdersRequest();
            OrdersResponse response = client.execute(request);
            List<OrderDT> orders = response.getOrders();
            Collections.sort(orders, Collections.reverseOrder(new Comparator<OrderDT>() {
                @Override
                public int compare(OrderDT o1, OrderDT o2) {
                    return Integer.valueOf(o1.getId()).compareTo(Integer.valueOf(o2.getId()));
                }
            }));
            for (OrderDT tmp : orders) {
                OrderDT order = client.execute(new OrderRequest(tmp.getId())).getOrder();
                if (helper.isNotBilled(order)) {
                    AddressDT address = order.getShipping().getAddress();
                    NameDT name = address.getName();
                    String email = order.getEmail();
                    List<String> parts = new ArrayList<String>();
                    parts.add(order.getBuyer());
                    String fullName = name.getFull();
                    int lastNamePos = fullName.lastIndexOf(' ');
                    parts.add(fullName.substring(0, lastNamePos));
                    parts.add(fullName.substring(lastNamePos + 1));
                    parts.add(email);
                    String[] fullAddress = address.getFull().split("\r\n");
                    for (String addressPart : fullAddress) {
                        parts.add(addressPart);
                    }
                    parts.add(address.getCountry().name());
                    output.add(parts.toArray(new String[parts.size()]));
                } else {
                    break;
                }
            }
        } finally {
            client.close();
        }
        FileOutputStream fis = null;
        OutputStreamWriter osw = null;
        CSVWriter csvw = null;
        try {
            fis = new FileOutputStream(new File("kunden.csv"));
            osw = new OutputStreamWriter(fis, "Windows-1252");
            csvw = new CSVWriter(osw, ';', CSVWriter.DEFAULT_QUOTE_CHARACTER, "\r\n");
            csvw.writeAll(output);
        } finally {
            IOUtils.closeQuietly(osw);
            IOUtils.closeQuietly(fis);
            if (null != csvw) {
                csvw.close();
            }
        }
    }
}
