package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * <title>"JConsole"-Javaģ�����̨</title>
 * <p>
 * ���������Javaʵ��Windows CMD����̨��ͨ����õ�ǰ���н��̣�
 * ִ�����������Ϣ��ʵ�ֽ���ʽ��CMD����̨��<br>
 * <br>
 * ����и��뷨,��Javaʵ����Windows ��CMD����̨һ��,���Խ��н���ʽ������������в���,
 * ��������д�˸��򵥵����ӡ�
 *
 * </p>
 * @author κ����
 * @version 0.9
 */
public class JConsole {
	/**
	 * ��ȡִ�е�ǰ����Ľ���
	 *
	 * @param strCommand
	 *            ��������
	 * @return pro Precess
	 */
	private Process getProcess(String strCommand) {
		Process pro = null;
		try {
			// ���е�ǰ�����ô����н���
			pro = Runtime.getRuntime().exec(strCommand);
		} catch (IOException e) {
			// when cause IOException ,Print the Exception message;
			System.out.println("Run this command:" + strCommand + "cause Exception ,and the message is " + e.getMessage());
		}
		// ������������Ľ���
		return pro;
	}

	/**
	 * ��ȡ��ǰ���̱�׼������
	 *
	 * @param pro
	 *            ��ǰ����
	 * @return in InputStream
	 */
	public InputStream getInputSreamReader(Process pro) {
		InputStream in = null;

		// ��õ�ǰ���̵�������
		in = pro.getInputStream();

		// ����������
		return in;
	}

	/**
	 * ��ȡ��ǰ���̵Ĵ���������
	 *
	 * @param pro
	 *            ��ǰ����
	 * @return error InputStream
	 */
	public InputStream getErrorSreamReader(Process pro) {
		InputStream error = null;

		// ��õ�ǰ���̵Ĵ�����
		error = pro.getErrorStream();

		// ���ش�����
		return error;
	}

	/**
	 * ��ӡ�������е����ݵ���׼���
	 *
	 * @param in
	 *            InputStream ������
	 */
	public void printMessage(final InputStream in) {
		Runnable runnable = new Runnable() {

			/*
			 * (non-Javadoc)
			 *
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				try {
					/*
					 * int ch; do { ch = in.read();
					 *
					 * if (ch < 0) return; System.out.print((char) ch);
					 * System.out.flush(); } while (true);
					 */
					BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
					String lines;
					while (true) {
						lines = buffer.readLine();
						if (lines == null)
							return;
						System.out.println("" + lines);
						System.out.flush();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		};
		Thread thread;
		thread = new Thread(runnable);
		thread.start();
	}

	/**
	 * ��ʼ��CMD����̨��
	 */
	public void run() {
		Process pro;

		InputStream input;
		pro = getProcess("cmd");

		input = getInputSreamReader(pro);

		printMessage(input);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// �����ӽ���
		Process pro = null;

		Scanner in = new Scanner(System.in);

		// ������
		InputStream input;

		// ������
		InputStream err;

		JConsole jconsole = new JConsole();

		// ��ʼ��CMD����̨
		jconsole.run();

		// ��������ȡ��Ҫִ�е��������ӡ���������Ϣ
		while (in.hasNextLine()) {

			String strCMD = in.nextLine();

			if (strCMD.equals("exit")) {
				System.exit(0);
			}

			// ִ�����������
			pro = jconsole.getProcess("cmd /E:ON /c " + strCMD);

			input = jconsole.getInputSreamReader(pro);

			err = jconsole.getErrorSreamReader(pro);

			// ��ӡ�������н����Ϣ
			int strTmp = input.read();
			if (strTmp == -1) {
				jconsole.printMessage(err);
			} else {
				jconsole.printMessage(input);
			}
		}
	}

}