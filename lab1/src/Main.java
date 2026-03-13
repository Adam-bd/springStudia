import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        double a, b, c;
        int option;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Wpisz, który kąt chcesz zmierzyć: (1) w łokciu, (2) w kolanie, (3) w biodrze ");
        option = scanner.nextInt();

        if(option == 1){
            System.out.println("Podaj długość (cm) od barku do łokcia: ");
            a = scanner.nextDouble();
            System.out.println("Podaj długość (cm) od łokcia do nadgarstka: ");
            b = scanner.nextDouble();
            System.out.println("Podaj długość (cm) bark-nadgarstek: ");
            c = scanner.nextDouble();
        } else if(option == 2) {
            System.out.println("Podaj długość (cm) od biodra do kolana: ");
            a = scanner.nextDouble();
            System.out.println("Podaj długość (cm) od kolana do kostki: ");
            b = scanner.nextDouble();
            System.out.println("Podaj długość (cm) biodra-kostka: ");
            c = scanner.nextDouble();
        } else if(option == 3){
            System.out.println("Podaj długość (cm) od barku do biodra: ");
            a = scanner.nextDouble();
            System.out.println("Podaj długość (cm) od biodra do kolana: ");
            b = scanner.nextDouble();
            System.out.println("Podaj długość (cm) bark-kolano: ");
            c = scanner.nextDouble();
        } else {
            System.out.println("Nie ma takiej opcji!");
            return;
        }

        double cos = (Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b);
        double radians = Math.acos(cos);
        double degrees = Math.toDegrees(radians);

        System.out.println("Kąt wynosi: " + degrees);

        if(degrees >= 90 && degrees <= 110) {
            System.out.println("Kąt ten mieści się w zalecanych normach.");
        } else {
            System.out.println("Kąt ten nie mieści się w zalecanych normach!");
        }
    }
}