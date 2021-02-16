package com.qainfotech.tap.training.snl.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BoardTest {
	Board board;

	@BeforeMethod
	public void methodSetUp() throws FileNotFoundException, UnsupportedEncodingException, IOException {
		this.board = new Board();
	}

	@Test
	public void initBoardAnEmptyBoard() throws FileNotFoundException, UnsupportedEncodingException, IOException {
		// #1 initialize the board construction.
		this.board = new Board();
		// Expected: new board created
		// if a file exists with name BOARD_UUID.Board i.e. new board is created.
		Assert.assertTrue(Files.exists(Paths.get(board.getUUID() + ".board")));
		// Assertion: Match Actual == Expected
	}

	@Test
	public void registerPlayer_should_add_a_new_player_to_the_board()
			throws FileNotFoundException, UnsupportedEncodingException, IOException, PlayerExistsException,
			GameInProgressException, MaxPlayersReachedExeption {
		// #2 Register new player on board by name "SUHAIL"
		UUID suhailUUID = this.board.registerPlayer("SUHAIL");
		// Expected : new player by name "SUHAIL" exists on board
		Assert.assertEquals(this.board.getData().getJSONArray("players").getJSONObject(0).getString("name"), "SUHAIL");

		Assert.assertEquals(this.board.getData().getJSONArray("players").getJSONObject(0).get("uuid").toString(),
				suhailUUID.toString());

	}

	@Test(expectedExceptions = (PlayerExistsException.class))
	public void registerPlayer_should_throws_Exception_when_a_player_with_name_exists()
			throws FileNotFoundException, UnsupportedEncodingException, IOException, PlayerExistsException,
			GameInProgressException, MaxPlayersReachedExeption {

		// Add player 1 with name "SUHAIL"
		this.board.registerPlayer("SUHAIL");

		// Add player 2 with name "SUHAIL"
		this.board.registerPlayer("SUHAIL");
		// expect: Exception is thrown

	}

	@Test(expectedExceptions = (MaxPlayersReachedExeption.class))
	public void registerPlayer_should_throws_Exception_when_try_to_register_5th_player()
			throws FileNotFoundException, UnsupportedEncodingException, PlayerExistsException, GameInProgressException,
			MaxPlayersReachedExeption, IOException {
		// Add player 1 with name "SUHAIL"
		this.board.registerPlayer("SUHAIL");

		// Add player 2 with name "Rajiv"
		this.board.registerPlayer("Rajiv");

		// Add player 3 with name "Anurag"
		this.board.registerPlayer("Anurag");

		// Add player 4 with name "Vikas"
		this.board.registerPlayer("Vikas");

		// Add player 5 with name "player5"
		this.board.registerPlayer("player5");
	}

	@Test
	public void registerPlayer_All_players_are_on_initial_position_0th_step()
			throws FileNotFoundException, UnsupportedEncodingException, PlayerExistsException, GameInProgressException,
			MaxPlayersReachedExeption, IOException {

		// Add player 1 with name "SUHAIL"
		this.board.registerPlayer("SUHAIL");

		// Add player 2 with name "Rajiv"
		this.board.registerPlayer("Rajiv");

		// Add player 3 with name "Anurag"
		this.board.registerPlayer("Anurag");

		// Add player 4 with name "Vikas"
		this.board.registerPlayer("Vikas");

		Assert.assertEquals(this.board.getData().getJSONArray("players").getJSONObject(0).get("position"), 0);
		Assert.assertEquals(this.board.getData().getJSONArray("players").getJSONObject(1).get("position"), 0);
		Assert.assertEquals(this.board.getData().getJSONArray("players").getJSONObject(2).get("position"), 0);
		Assert.assertEquals(this.board.getData().getJSONArray("players").getJSONObject(3).get("position"), 0);

	}

	@Test(expectedExceptions = (GameInProgressException.class))
	public void registerPlayer_should_throws_Exception_when_try_to_register_on_a_board_where_players_have_already_started_movement()
			throws FileNotFoundException, UnsupportedEncodingException, PlayerExistsException, GameInProgressException,
			MaxPlayersReachedExeption, IOException, InvalidTurnException {
		// Add player 1 with name "SUHAIL"
		UUID suhailUUID = this.board.registerPlayer("SUHAIL");

		// Add player 2 with name "Rajiv"
		this.board.registerPlayer("Rajiv");

		// Add player 3 with name "Anurag"
		this.board.registerPlayer("Anurag");

		// roll dice in order i.e. rolled by player 1
		board.rollDice(suhailUUID);

		// Try to Add player 4 with name "Vikas" after starting the game
		this.board.registerPlayer("Vikas");
		// expect: Exception is thrown
	}

	@Test(expectedExceptions = (InvalidTurnException.class))
	public void roll_dice_throw_an_Exception_when_incorrect_order_player_uuid_is_passed()
			throws FileNotFoundException, UnsupportedEncodingException, PlayerExistsException, GameInProgressException,
			MaxPlayersReachedExeption, IOException, InvalidTurnException {
		// Add player 1 with name "SUHAIL"
		this.board.registerPlayer("SUHAIL");

		// Add player 2 with name "Rajiv"
		this.board.registerPlayer("Rajiv");

		// Add player 3 with name "Anurag"
		UUID anuragUUID = this.board.registerPlayer("Anurag");

		// Add player 4 with name "Vikas"
		this.board.registerPlayer("Vikas");

		// roll dice in unordered way i.e. direct rolled by player 3
		board.rollDice(anuragUUID);
		// expect: Exception is thrown
	}

	@Test
	public void roll_the_dice_of_the_turn_player_and_make_move_on_the_board_per_the_outcome()
			throws FileNotFoundException, UnsupportedEncodingException, PlayerExistsException, GameInProgressException,
			MaxPlayersReachedExeption, IOException, InvalidTurnException {
		// Add player 1 with name "SUHAIL"
		UUID suhailUUID = this.board.registerPlayer("SUHAIL");

		// Add player 2 with name "Rajiv"
		this.board.registerPlayer("Rajiv");

		// Add player 3 with name "Anurag"
		this.board.registerPlayer("Anurag");

		// Add player 4 with name "Vikas"
		this.board.registerPlayer("Vikas");

		// Before the dice roll
		int before_roll = (int) this.board.getData().getJSONArray("players").getJSONObject(0).get("position");

		// roll dice in order i.e. rolled by player 1
		board.rollDice(suhailUUID);

		// Expect : before and after dice roll position value should be change
		Assert.assertTrue(
				before_roll != (int) this.board.getData().getJSONArray("players").getJSONObject(0).get("position"));
	}

	@Test(expectedExceptions = (NoUserWithSuchUUIDException.class))
	public void deletes_player_throw_an_Exception_when_list_uuid_not_matches()
			throws FileNotFoundException, UnsupportedEncodingException, PlayerExistsException, GameInProgressException,
			MaxPlayersReachedExeption, IOException, NoUserWithSuchUUIDException {

		//---------------------------------------ERROR-----------------------------------------------------


		// Change Board.java line(104) in delete test case
		// throw error with if value of if condition is:
		// condition:player.getString("uuid").equals(playerUuid.toString()
		// run correct with if  value of if condition is :
		// condition:player.get("uuid").toString().equals(playerUuid.toString()

		// Add player 1 with name "SUHAIL"
		this.board.registerPlayer("SUHAIL");

		// Add player 2 with name "Rajiv"
		this.board.registerPlayer("Rajiv");

		// Add player 3 with name "Anurag"
		this.board.registerPlayer("Anurag");

		// Add player 4 with name "Vikas"
		this.board.registerPlayer("Vikas");

		// try to delete player with wrong UUID
		this.board.deletePlayer(UUID.randomUUID());
		// expect: Exception is thrown
	}

	@Test(expectedExceptions = (NoUserWithSuchUUIDException.class))
	public void deletes_player_from_list_if_uuid_matches()
			throws FileNotFoundException, UnsupportedEncodingException, PlayerExistsException, GameInProgressException,
			MaxPlayersReachedExeption, IOException, NoUserWithSuchUUIDException {

		//---------------------------------------ERROR-----------------------------------------------------


		// Change Board.java line(104) in delete test case
		// throw error with if value of if condition is:
		// condition:player.getString("uuid").equals(playerUuid.toString()
		// run correct with if  value of if condition is :
		// condition:player.get("uuid").toString().equals(playerUuid.toString()

		// Add player 1 with name "SUHAIL"
		this.board.registerPlayer("SUHAIL");

		// Add player 2 with name "Rajiv"
		this.board.registerPlayer("Rajiv");

		// Add player 3 with name "Anurag"
		this.board.registerPlayer("Anurag");

		// Add player 4 with name "Vikas"
		UUID vikasUUID = this.board.registerPlayer("Vikas");

		// delete player using uuid
		this.board.deletePlayer(vikasUUID);

		// if player deleted then throw exception
		this.board.deletePlayer(vikasUUID);
		// expect: Exception is thrown
	}

}
