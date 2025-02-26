//package il.co.ilrd.rps;
//
//import il.co.ilrd.gatewayserver.Message;
//import org.junit.jupiter.api.Test;
//
//
//public class RPSTest {
//    SimpleRps rps = new SimpleRps();
//
//    @Test
//    public void testRegCompany() {
//        rps.handleRequest(new Message("RegCompany&companyName@Apple", null, null));
//        rps.handleRequest(new Message("RegCompany&companyName@Microsoft", null, null));
//        rps.handleRequest(new Message("RegCompany&companyName@Sony", null, null));
//    }
//
//    @Test
//    public void testRegProduct() {
//        rps.handleRequest(new Message("RegProduct&productName@iphone", null, null));
//        rps.handleRequest(new Message("RegProduct&productName@xbox", null, null));
//        rps.handleRequest(new Message("RegProduct&productName@smartTV", null, null));
//    }
//
//    @Test
//    public void testRegDevice() {
//        rps.handleRequest(new Message("RegDevice&deviceName@Tzur's_iphone#deviceOwner@TzurB", null, null));
//        rps.handleRequest(new Message("RegDevice&deviceName@Elad's_pc#deviceOwner@EladZ", null, null));
//        rps.handleRequest(new Message("RegDevice&deviceName@Simon's_smartWatch#deviceOwner@SimonG", null, null));
//    }
//
//    @Test
//    public void testRegUpdate() {
//        rps.handleRequest(new Message("RegUpdate&update@charge_battery", null, null));
//        rps.handleRequest(new Message("RegUpdate&update@gas_low", null, null));
//        rps.handleRequest(new Message("RegUpdate&update@new_update_available", null, null));
//    }
//
//    @Test
//    public void testRegCombined() {
//        rps.handleRequest(new Message("RegCompany&companyName@PizzaHut", null, null));
//        rps.handleRequest(new Message("RegDevice&deviceName@pizza_tray", null, null));
//        rps.handleRequest(new Message("RegProduct&productName@peperonni_pizza", null, null));
//        rps.handleRequest(new Message("RegUpdate&update@eat_pizza", null, null));
//        rps.handleRequest(new Message("RegCompany&companyName@Mcdonalds", null, null));
//        rps.handleRequest(new Message("RegUpdate&update@eat_burger", null, null));
//        rps.handleRequest(new Message("RegProduct&productName@Double_Cheesburger", null, null));
//        rps.handleRequest(new Message("RegDevice&deviceName@fryer#deviceOwner@Mcdonalds", null, null));
//    }
//}
