package missao_pratica_1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

abstract class Pessoa implements Serializable {
    protected int id;
    protected String nome;

    public Pessoa(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public abstract void exibir();
}

class PessoaFisica extends Pessoa {
    private final String cpf;
    private final int idade;

    public PessoaFisica(int id, String nome, String cpf, int idade) {
        super(id, nome);
        this.cpf = cpf;
        this.idade = idade;
    }

    @Override
    public void exibir() {
        System.out.println("ID: " + id + ", Nome: " + nome + ", CPF: " + cpf + ", Idade: " + idade);
    }
}

class PessoaJuridica extends Pessoa {
    private final String cnpj;

    public PessoaJuridica(int id, String nome, String cnpj) {
        super(id, nome);
        this.cnpj = cnpj;
    }

    @Override
    public void exibir() {
        System.out.println("ID: " + id + ", Nome: " + nome + ", CNPJ: " + cnpj);
    }
}

class Repositorio<T extends Pessoa> {
    private List<T> lista = new ArrayList<>();

    public void inserir(T pessoa) { lista.add(pessoa); }
    public void alterar(int id, T novaPessoa) {
        lista.replaceAll(p -> p.getId() == id ? novaPessoa : p);
    }
    public void excluir(int id) { lista.removeIf(p -> p.getId() == id); }
    public T obter(int id) { return lista.stream().filter(p -> p.getId() == id).findFirst().orElse(null); }
    public List<T> obterTodos() { return lista; }

    public void persistir(String arquivo) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            out.writeObject(lista);
        }
    }

    @SuppressWarnings("unchecked")
    public void recuperar(String arquivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            lista = (List<T>) in.readObject();
        }
    }
}

public class CadastroPOO {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Repositorio<PessoaFisica> repoFisica = new Repositorio<>();
            Repositorio<PessoaJuridica> repoJuridica = new Repositorio<>();
            
            while (true) {
                System.out.println("1. Incluir | 2. Alterar | 3. Excluir | 4. Exibir pelo ID | 5. Exibir todos | 6. Salvar | 7. Recuperar | 0. Sair");
                int opcao = scanner.nextInt(); scanner.nextLine();
                if (opcao == 0) break;
                
                switch (opcao) {
                    case 1 -> {
                        System.out.println("Física (1) ou Jurídica (2)?");
                        int tipo = scanner.nextInt(); scanner.nextLine();
                        System.out.print("ID: ");
                        int id = scanner.nextInt(); scanner.nextLine();
                        System.out.print("Nome: ");
                        String nome = scanner.nextLine();
                        if (tipo == 1) {
                            System.out.print("CPF: ");
                            String cpf = scanner.nextLine();
                            System.out.print("Idade: ");
                            int idade = scanner.nextInt(); scanner.nextLine();
                            repoFisica.inserir(new PessoaFisica(id, nome, cpf, idade));
                        } else {
                            System.out.print("CNPJ: ");
                            String cnpj = scanner.nextLine();
                            repoJuridica.inserir(new PessoaJuridica(id, nome, cnpj));
                        }
                    }
                    case 5 -> {
                        System.out.println("Pessoas Físicas:");
                        repoFisica.obterTodos().forEach(Pessoa::exibir);
                        System.out.println("Pessoas Jurídicas:");
                        repoJuridica.obterTodos().forEach(Pessoa::exibir);
                    }
                    case 6 -> {
                        try {
                            repoFisica.persistir("fisica.bin");
                            repoJuridica.persistir("juridica.bin");
                        } catch (IOException e) {
                            System.out.println("Erro ao salvar: " + e.getMessage());
                        }
                    }
                    case 7 -> {
                        try {
                            repoFisica.recuperar("fisica.bin");
                            repoJuridica.recuperar("juridica.bin");
                        } catch (IOException | ClassNotFoundException e) {
                            System.out.println("Erro ao recuperar: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
