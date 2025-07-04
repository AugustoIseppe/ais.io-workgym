import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';
import 'package:workgym/app/controllers/login_controller.dart';
import 'package:workgym/app/pages/plans/plans_page.dart';
import 'package:workgym/app/pages/schedules/schedules_page.dart';
import 'package:workgym/app/pages/userdata/user_data.dart';
import 'package:workgym/app/pages/weekday/week_day.dart';
import 'package:workgym/app/widgets/home_card_component.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  Widget build(BuildContext context) {
    final user = Provider.of<LoginController>(context).user;
    return Scaffold(
      drawer: Drawer(
        shadowColor: Colors.black,
        elevation: 10,
        backgroundColor: const Color.fromARGB(255, 13, 26, 45),
        child: ListView(
          padding: EdgeInsets.zero,
          children: [
            UserAccountsDrawerHeader(
              decoration: const BoxDecoration(
                color: Color.fromARGB(255, 13, 26, 45),
              ),
              accountName: Text(
                user?.login ?? '',
                style: GoogleFonts.merriweather(
                  textStyle: TextStyle(
                    color: Colors.white,
                    letterSpacing: .5,
                    // fontSize: 25,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              accountEmail: Text(
                user?.email ?? '',
                style: GoogleFonts.merriweather(
                  textStyle: TextStyle(
                    color: Colors.white,
                    letterSpacing: .5,
                    // fontSize: 25,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              currentAccountPicture: CircleAvatar(
                backgroundColor: Colors.white,
                child: Text(
                  user?.login.substring(0, 1).toUpperCase() ?? '',
                  style: const TextStyle(fontSize: 40.0, color: Colors.black),
                ),
              ),
            ),
            ListTile(
              leading: const Icon(Icons.settings, color: Colors.white),
              title: Text(
                'Logout',
                style: GoogleFonts.merriweather(
                  textStyle: TextStyle(
                    color: Colors.white,
                    letterSpacing: .5,
                    // fontSize: 25,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              onTap: () async {
                final confirmLogout = await showDialog<bool>(
                  context: context,
                  builder: (BuildContext context) {
                    return AlertDialog(
                      title: const Text('Confirmar Logout'),
                      content: const Text('Você tem certeza que deseja sair?'),
                      actions: [
                        TextButton(
                          onPressed: () => Navigator.of(context).pop(false),
                          child: const Text('Cancelar'),
                        ),
                        TextButton(
                          onPressed: () => Navigator.of(context).pop(true),
                          child: const Text('Sair'),
                        ),
                      ],
                    );
                  },
                );

                if (confirmLogout == true) {
                  context.read<LoginController>().logout(context);
                  Navigator.pushReplacementNamed(context, '/login-page');
                }
              },
            ),
          ],
        ),
      ),
      body: SafeArea(
        child: Container(
          padding: const EdgeInsets.all(20),
          height: double.infinity,
          width: double.infinity,
          decoration: const BoxDecoration(
            image: DecorationImage(
              image: AssetImage('assets/images/bg-home-page.png'),
              fit: BoxFit.cover,
              colorFilter: ColorFilter.mode(
                Color.fromARGB(255, 13, 55, 118),
                // BlendMode.multiply,
                BlendMode.modulate,
              ),
            ),
          ),
          child: SingleChildScrollView(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                const SizedBox(height: 20),
                SizedBox(
                  //container para o logo e o texto de boas-vindas
                  child: Column(
                    children: [
                      Text(
                        'Bem-vindo,',
                        style: GoogleFonts.merriweather(
                          textStyle: TextStyle(
                            color: Colors.white,
                            letterSpacing: .5,
                            fontSize: 40,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      Text(
                        '${user?.login}!',
                        style: GoogleFonts.merriweather(
                          textStyle: TextStyle(
                            color: Colors.white,
                            letterSpacing: .5,
                            fontSize: 30,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 40),
                //container com 2 cards
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(20),
                  decoration: BoxDecoration(
                    color: Colors.white.withValues(alpha: 0.1),
                    borderRadius: BorderRadius.circular(20),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.black.withAlpha(50),
                        blurRadius: 10,
                        offset: const Offset(0, 5),
                      ),
                    ],
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceAround,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          buildHomeCard(
                            label: 'Treinos',
                            icon: Icons.fitness_center,
                            context: context,
                            destination: const WeekDay(),
                          ),
                          buildHomeCard(
                            label: 'Dados do Usuário',
                            icon: Icons.person,
                            context: context,
                            destination: const UserData(),
                          ),
                        ],
                      ),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceAround,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          buildHomeCard(
                            label: 'Horários',
                            icon: Icons.access_time,
                            context: context,
                            destination: const SchedulesPage(),
                          ),
                          buildHomeCard(
                            label: 'Planos',
                            icon: Icons.card_membership,
                            context: context,
                            destination: const PlansPage(),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 40),
                //container com imagem de fundo
                Image.asset(
                  'assets/images/testelogo.png',
                  fit: BoxFit.fitWidth,
                  width: 200,
                  height: 150,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
